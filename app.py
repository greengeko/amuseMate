import requests
from flask import Flask, jsonify, request
import mindsdb_sdk

con = mindsdb_sdk.connect('http://127.0.0.1:47334')
databases = con.databases.list()

database = databases[0]

model = con.models.get('lightfm_model')
suggested_items = set()
app = Flask(__name__)

@app.route('/')
def suggest_activity():
    try:
        suggested_items_json = request.args.get('suggested_items')
        suggested_items_list = suggested_items_json.split(',') if suggested_items_json else []

        # Update suggested_items set
        if suggested_items_list:
            suggested_items.clear()
            for item in suggested_items_list:
                suggested_items.add(int(item))

        # Define the user (hardcoded for now)
        user_id = 4

        # Make predictions using the MindsDB model
        predictions = model.predict({'userId': user_id})

        recommendations_list = predictions.to_dict(orient='records')

        if recommendations_list:
            item_id = recommendations_list[0]['item_id']

            # Check if the item_id has been suggested previously
            while item_id in suggested_items:
                # If so, get the next prediction
                recommendations_list.pop(0)
                if not recommendations_list:
                    break
                item_id = recommendations_list[0]['item_id']

            if recommendations_list:
                # Add the suggested item_id to the set of suggested items to exclude it
                suggested_items.add(item_id)

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
            else:
                return jsonify({'error': 'No recommendations available'})
        else:
            return jsonify({'error': 'No predictions available'})

    except Exception as e:
        return jsonify({'error': str(e)})


if __name__ == '__main__':
    app.run()
