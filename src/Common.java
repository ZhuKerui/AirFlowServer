
public class Common {
	public class Norms{
		public static final int SERVER_PORT = 7777;
	}
	
	public class PacketParams {
        public static final String USER_NAME = "user_name";
        public static final String PASSWORD = "password";
        public static final String ERRORCODE = "error";
        public static final String INSTRUCTION = "instruction";
        public static final String OPERATION = "operation";
        public static final String DATA = "data";
    }

    public class ErrorCode {
        public static final int NON_EXIST_USER = 0;
        public static final int NAME_OCCUPIED = 1;
        public static final int WRONG_PASSWORD = 2;
        public static final int SERVER_ERROR = 3;
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
