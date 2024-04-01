import requests
from flask import Flask, jsonify
import mindsdb_sdk

con = mindsdb_sdk.connect('http://127.0.0.1:47334')
databases = con.databases.list()

database = databases[0]  # Database type object

model = con.models.get('lightfm_model')

app = Flask(__name__)


@app.route('/')
def suggest_activity():
    try:
        # Define the query for suggestion
        # query = "SELECT b.* FROM lightfm_suggestions AS b WHERE userId = 4 USING recommender_type = 'user_item'"

        user_id = 4

        # Make predictions using the MindsDB model
        predictions = model.predict({'userId': user_id})

        recommendations_list = predictions.to_dict(orient='records')
        item_id = recommendations_list[0]['item_id']

        # Define the endpoint for MindsDB API
        endpoint = 'http://127.0.0.1:47334/api/sql/query'

        query = f"SELECT * FROM files.activities WHERE id = {item_id}"

        activity = requests.post(endpoint, json={'query': query})

        if activity.status_code == 200:
            result = activity.json()
            if result:
                return jsonify({'activity': result})
            else:
                return jsonify({'error': 'No activity found'})
        else:
            return jsonify({'error': f'Failed to query MindsDB: {activity.text}'})

    except Exception as e:
        return jsonify({'error': str(e)})


if __name__ == '__main__':
    app.run()
