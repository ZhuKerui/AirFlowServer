FROM python:3.7
ENV PYTHONUNBUFFERED 1
RUN mkdir /code
WORKDIR /code
ADD ./django_req.txt /code/
RUN pip3 install -r django_req.txt