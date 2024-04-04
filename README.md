
# AmuseMate
![image](https://github.com/greengeko/amuseMate/assets/25327740/64d74929-0bd0-47b3-a13a-5f86e928872f)

With a single tap, discover a world of exciting activities curated to bring joy and fun to your day. From outdoor adventures to creative projects, AmuseMate offers a diverse range of options to suit every mood and occasion.

Note: the current data is mocked with Mockaroo and does not contain real activities, feel free to contribute

## Features

- Disable activities with major age necessary

## Demo

TODO


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
Step 5 (Launch the app.py script and your server is set up and running!🎉 )


## Tech Stack

**Client:** Flagsmith, Android, Java,

**Server:** MindsDB, LightFM, Python, Makefile, Docker

## Authors

- [@greengeko](https://www.github.com/greengeko)
