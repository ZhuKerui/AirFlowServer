import MySQLdb

class DataBaseManager:
    def __init__(self, host, port, user, passwd, database) -> None:
        self.conn= MySQLdb.connect(
        host=host,
        port = port,
        user = user,
        passwd = passwd,
        db = database,
        )

    def clear_db(self):
        try:
            cur = self.conn.cursor()
            cur.execute("SHOW TABLES")
            result = cur.fetchall()
            for item in result:
                cur.execute("DROP TABLE " + item[0])
            print("Database Cleared")
            cur.execute("CREATE TABLE USER_INFO(USER_NAME VARCHAR(50))")
            cur.execute("INSERT INTO USER_INFO VALUES('_default')")
            cur.execute("CREATE TABLE _default(AIR_FLOW_FILE VARCHAR(100))")
            print("Basic tables created")

            cur.close()
            self.conn.commit()
            return True
        except:
            return False
    
    def isUserExist(self, user_name:str):
        try:
            cur = self.conn.cursor()
            result_num = cur.execute("SELECT * FROM USER_INFO WHERE USER_NAME = %s" % user_name)
            cur.close()
            return result_num == 1
        except:
            return False

    def addUser(self, user_name):
        try:
            cur = self.conn.cursor()
            cur.execute("CREATE TABLE " + user_name + "(AIR_FLOW_FILE VARCHAR(100))")
            cur.execute("INSERT INTO USER_INFO VALUES(%s)" % user_name)
            cur.close()
            self.conn.commit()
            return True
        except:
            return False

    def insertData(self, user_name, file_name):
        if user_name and file_name and self.isUserExist(user_name):
            try:
                cur = self.conn.cursor()
                cur.execute("INSERT INTO " + user_name + " VALUES(%s)" % file_name)
                cur.close()
                self.conn.commit()
                return True
            except:
                return False
        else:
            return False

    def close_db(self):
        self.conn.close()