from socket import *
import traceback
import sys
sys.path.append('..')
from dbManager.DataBaseManager import *
from socket_common import *
from time import time, sleep
import json
import os
import io
import numpy as np

class ServerSocket:
    def __init__(self) -> None:
        dbPath = '../dbManager'
        self.DBManager = None
        self.storepath = "%s/%s" % (dbPath, 'AirFlowData')
        cnt = 0
        while self.DBManager is None:
            try:
                self.DBManager = DataBaseManager(
                        host = 'database',
                        port = 3306,
                        user = 'root',
                        passwd = 'root',
                        database = 'AirFlow',
                        storepath = self.storepath
                        )
            except:
                print('Try Connection %d fail' % cnt)
                cnt += 1
                self.DBManager = None
                sleep(10)
        self.DBManager.clear_db()
        self.HOST = ''
        self.PORT = SERVER_PORT
        self.BUFSIZ = 1024
        self.ADDR = (self.HOST,self.PORT)
        self.tcpSerSock = socket(AF_INET,SOCK_STREAM)
        self.tcpSerSock.bind(self.ADDR)

    def run_server(self):
        self.tcpSerSock.listen(5)

        while True:
            print('waiting for connection...')
            try:
                tcpCliSock, addr = self.tcpSerSock.accept()
                print('...connnecting from:', addr)

                input_buffer = []
                while True:
                    data = tcpCliSock.recv(self.BUFSIZ)
                    if not data:
                        break
                    input_buffer.append(str(data, encoding='utf-8'))
                    # tcpCliSock.send(('[%s] %s' % (ctime(), data)).encode())
                input_str = ''.join(input_buffer)
                msgInJsonObj = json.loads(input_str)
                user_name = msgInJsonObj[PacketParams.USER_NAME]
                operation = msgInJsonObj[PacketParams.OPERATION]

                if operation == Operation.LOGIN:
                    if self.DBManager.isUserExist(user_name):
                        self.returnSuccess(tcpCliSock)
                    else:
                        self.returnError(ErrorCode.NON_EXIST_USER, tcpCliSock)
                elif operation == Operation.REGISTER:
                    if self.DBManager.isUserExist(user_name):
                        self.returnError(ErrorCode.NAME_OCCUPIED, tcpCliSock)
                    elif self.DBManager.addUser(user_name):
                        self.returnSuccess(tcpCliSock)
                    else: 
                        self.returnError(ErrorCode.SERVER_ERROR, tcpCliSock)
                elif operation == Operation.UPLOAD:
                    packet_num = int(msgInJsonObj[PacketParams.PACKET_NUM])
                    packets = msgInJsonObj[PacketParams.PACKETS]
                    i = 0
                    for i, packet in enumerate(packets):
                        file_name = "%f_%d.npy" % (time(), i)
                        path = "%s/%s/%s" % (self.storepath, user_name, file_name)
                        if self.DBManager.insertData(user_name, file_name):
                            packet_str = str(packet).replace("'", '"')
                            packet_dict = json.loads(packet_str)
                            data_str = packet_dict[PacketParams.AIRFLOW_DATA].strip('[]').split(', ')
                            data_float = map(float, data_str)
                            data = np.array(list(data_float))
                            np.save(path, data)
                        else:
                            break
                    if i == (packet_num - 1):
                        self.returnSuccess(tcpCliSock)
                    else:
                        self.returnError(ErrorCode.SERVER_ERROR, tcpCliSock)
                tcpCliSock.close()
            except Exception as e:
                print(e)
                traceback.print_exc()
        tcpSerSock.close()

    def returnSuccess(self, tcpCliSock):
        replyJsonObj = {PacketParams.INSTRUCTION : Instruction.APPROVED}
        tcpCliSock.send(json.dumps(replyJsonObj).encode())

    def returnError(self, errorCode, tcpCliSock):
        replyJsonObj = {PacketParams.INSTRUCTION : Instruction.REJECTED, PacketParams.ERRORCODE : errorCode}
        tcpCliSock.send(json.dumps(replyJsonObj).encode())

if __name__ == "__main__":
    server_socket = ServerSocket()
    server_socket.run_server()