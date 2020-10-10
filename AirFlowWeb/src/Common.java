
public class Common {
	public class Norms{
		public static final int SERVER_PORT = 7777;
	}
	
	public class PacketParams {
		public static final String USER_NAME = "user_name";
        public static final String OPERATION = "operation";
        public static final String PACKET_NUM = "packet_num";
        public static final String PACKETS = "packets";
        public static final String AIRFLOW_DATA = "airflow_data";
        public static final String INSTRUCTION = "instruction";
        public static final String ERRORCODE = "error";
    }

    public class ErrorCode {
        public static final int NON_EXIST_USER = 0;
        public static final int NAME_OCCUPIED = 1;
        public static final int SERVER_ERROR = 2;
    }

    public class Instruction {
        public static final int APPROVED = 0;
        public static final int REJECTED = 1;
    }

    public class Operation {
        public static final int LOGIN = 0;
        public static final int REGISTER = 1;
        public static final int UPLOAD = 2;
    }
}
