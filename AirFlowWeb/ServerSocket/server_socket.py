from ast import dump
from socket import *
from dbManager.DataBaseManager import *
from ServerSocket.socket_common import *
from time import time
import json
import os
import io

class ServerSocket:
    def __init__(self) -> None:
        self.DBManager = DataBaseManager(
                host='database',
                port = 3306,
                user='root',
                passwd='root',
                database ='AirFlow',
                )
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
            tcpCliSock, addr = self.tcpSerSock.accept()
            print('...connnecting from:', addr)

            input_buffer = []
            while True:
                data = tcpCliSock.recv(self.BUFSIZ)
                if not data:
                    break
                input_buffer.append(data)
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
                    os.mkdir(user_name)
                    self.returnSuccess(tcpCliSock)
                else: 
                    self.returnError(ErrorCode.SERVER_ERROR, tcpCliSock)
            elif operation == Operation.UPLOAD:
                packet_num = int(msgInJsonObj[PacketParams.PACKET_NUM])
                packets = msgInJsonObj[PacketParams.PACKETS]
                i = 0
                for i, packet in enumerate(packets):
                    file_name = "%f_%d.txt" % (time(), i)
                    path = "%s/%s" % (user_name, file_name)
                    if self.DBManager.insertData(user_name, file_name):
                        with io.open(path, 'w', encoding='utf-8') as dump_file:
                            dump_file.write(str(packet))
                    else:
                        break
                if i == packet_num:
                    self.returnSuccess(tcpCliSock)
                else:
                    self.returnError(ErrorCode.SERVER_ERROR, tcpCliSock)
            tcpCliSock.close()
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