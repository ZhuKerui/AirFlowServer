from django.shortcuts import render, redirect
from django.http import JsonResponse
import logging
import sys
sys.path.append('..')
from dbManager.DataBaseManager import *
from time import sleep

dbPath = '../dbManager'
DBManager = None
storepath = "%s/%s" % (dbPath, 'AirFlowData')
cnt = 0
while DBManager is None:
    try:
        DBManager = DataBaseManager(
            host = 'database',
            port = 3306,
            user = 'root',
            passwd = 'root',
            database = 'AirFlow',
            storepath = storepath
            )
    except:
        print('Try Connection %d fail' % cnt)
        cnt += 1
        DBManager = None
        sleep(10)
print('DBManager connect successfully')

def index(request):
    logging.debug('debug works')
    return render(request, 'index.html')

def login(request):
    global DBManager
    user_name = request.GET.get('username')
    logging.debug(user_name)
    if DBManager.isUserExist(user_name):
        request.session['username'] = user_name
        return redirect('homepage.html')
    else:
        return render(request, 'index.html')
# def search_paper(request):
#     keywords = request.GET.get('keywords')
#     is_strict = request.GET.get('is_strict')
#     fields = request.GET.get('field_select')
#     limit = request.GET.get('limit')
#     if limit:
#         limit = int(limit)
#         results = engine.search(keywords,fields,['title', 'abstract', 'id'], is_strict, highlight=True, limit=limit)
#     else:
#         results = engine.search(keywords,fields,['title', 'abstract', 'id'], is_strict, highlight=True)
#     content = []
#     for data in results:
#         content.append({"title":data['title'], "content":data['abstract'], "id":data['id']})

#     return JsonResponse(content, safe=False)

# def get_paper(request):
#     id_ = request.GET.get('id')
#     result = engine.search(id_, 'id', engine.fields)
#     data = result[0]
#     content = {}
#     for field in engine.fields:
#         content[field] = data[field]
#     return render(request, 'paper.html', content)