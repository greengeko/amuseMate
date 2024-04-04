
# AmuseMate
![image](https://github.com/greengeko/amuseMate/assets/25327740/64d74929-0bd0-47b3-a13a-5f86e928872f)

With a single tap, discover a world of exciting activities curated to bring joy and fun to your day. From outdoor adventures to creative projects, AmuseMate offers a diverse range of options to suit every mood and occasion.

Note: the current data is mocked with Mockaroo and does not contain real activities, feel free to contribute or clone and adapt to your own necessities.
The activities are suggested by using LightFM Engine integration in MindsDB (https://mindsdb.com/) which base the suggested activity choice on the past user-item ratings.
Some of the activities are marked as "adultContent" and for mock data those would be items 1,2,3. 

## Features

- Disable/Enable activities serverside with major age necessary.
  This was made using Flagsmith (https://www.flagsmith.com/) and the tought behind is to limit the activities based on the user's age.

## Demo



https://github.com/greengeko/amuseMate/assets/25327740/d5b02c2d-2e93-4e4d-9adc-5fa61e803f82

Some screenshots: 

<div style="display: flex;">
    <img src="https://github.com/greengeko/amuseMate/assets/25327740/4cfbf726-2c52-40c4-b02b-75d45f3acfcf" alt="Image 1" width="200" style="margin-right: 20px;">
    <img src="https://github.com/greengeko/amuseMate/assets/25327740/950b8b86-35d1-45e4-aa07-42fd45783cd4" alt="Image 2" width="200">
</div>


## Run Locally

Step 0 (Download or Clone this project)

Step 1 (Create a container with mindsdb running)
```bash
make create_mindsdb
```
Step 2 (Update/Install some package in the mindsb container such as lifghtfm)
```bash
make update_packages
```
Step 3 (Restart the container in order to apply the updates)
```bash
make restart_container
```
Step 4 (Launch the setup.py script in order to set up the MindsDB container with LighFM ML Engine and Model and upload the files contained in the /data folder)
```bash
make build_setup
```
Step 5 (Launch the app.py and your server is set up and running!🎉 )

Step 6 (Create the containers necessary for Flagsmith)
```bash
docker-compose -f docker-compose.yml up
```
Step 7 (Reach the GUI at http://localhost:8000/ and create a feature flag named "adultcontent")

Step 8 (Go to the identities tab of your flagsmith environment and copy your personal API Key)

Step 9 (Paste your API Key at line 39 in MainActivity.java and you are ready to launch your app! Tip:use Android Studio!)


## Tech Stack

**Client:** Flagsmith, Android, Java,

**Server:** MindsDB, LightFM, Python, Makefile, Docker

## Authors

- [@greengeko](https://www.github.com/greengeko)

