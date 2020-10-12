FROM python:3.7
ENV PYTHONUNBUFFERED 1
RUN mkdir /code
RUN mkdir /dbManager
WORKDIR /code
ADD ./socket_req.txt /code/
RUN pip3 install -r socket_req.txt