FROM ubuntu:latest

WORKDIR /usr/src/app

COPY setup.py .
COPY data ./data

RUN apt-get update && apt-get install -y python3-pip
RUN pip install requests flask mindsdb_sdk pandas

CMD ["/bin/sh"]