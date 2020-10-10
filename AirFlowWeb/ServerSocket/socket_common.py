SERVER_ADDR = '127.0.0.1'
SERVER_PORT = 7777

class PacketParams:
    USER_NAME = "user_name"
    OPERATION = "operation"
    PACKET_NUM = "packet_num"
    PACKETS = "packets"
    AIRFLOW_DATA = "airflow_data"
    INSTRUCTION = "instruction"
    ERRORCODE = "error"

class ErrorCode:
    NON_EXIST_USER = 0
    NAME_OCCUPIED = 1
    SERVER_ERROR = 2

class Instruction:
    APPROVED = 0
    REJECTED = 1

class Operation:
    LOGIN = 0
    REGISTER = 1
    UPLOAD = 2