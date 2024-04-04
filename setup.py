import requests

from flask import Flask
import mindsdb_sdk
import pandas as pd

con = mindsdb_sdk.connect('http://mindsdb_container:47334')
databases = con.databases.list()
files = con.get_database('files')
activities = pd.read_csv('data/activities.csv')
ratings = pd.read_csv('data/ratings.csv')

files.create_table('activities', activities)
files.create_table('ratings', ratings)

query = "CREATE ML_ENGINE lightfm FROM lightfm"
con.query(query)

url = 'http://mindsdb_container:47334/api/sql/query'

query2 = ("CREATE MODEL lightfm_model FROM files (SELECT * FROM ratings) PREDICT activityId USING engine = 'lightfm', "
          "item_id = 'activityId', user_id = 'userId', threshold = 4,n_recommendations = 15")
resp = requests.post(url, json={'query':query2}, verify=False)

app = Flask(__name__)

if __name__ == '__main__':
    app.run()
