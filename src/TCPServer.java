
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import com.mysql.cj.jdbc.Blob;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class TCPServer {
	
	private static ServerSocket myServerSocket;

	public static void main(String[] args) {
		
		if (!DataBaseManager.initDataBase()) {
			return;
		}
		
		try {
			myServerSocket = new ServerSocket(Common.Norms.SERVER_PORT);
			while (true) {
				Socket socket = myServerSocket.accept();
				new MsgHandler(socket).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myServerSocket != null) {
				try {
					myServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				myServerSocket = null;
			}
		}
	}
}

class MsgHandler extends Thread {
	
	private Socket mySocket;
	
	public MsgHandler(Socket socket) {
		this.mySocket = socket;
	}
	
	@Override
	public void run() {
		try {
			InputStream iStream = mySocket.getInputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] input_byte = new byte[1024];
			int len = 0;
			while ((len = iStream.read(input_byte)) != -1) {
				buffer.write(input_byte, 0, len);
			}
			
			String input_txt = buffer.toString();
			JSONObject msgInJsonObject = JSONObject.fromObject(input_txt);
			String user_name = msgInJsonObject.getString(Common.PacketParams.USER_NAME);
			int operation = msgInJsonObject.getInt(Common.PacketParams.OPERATION);
			
			DataBaseManager dataBase = new DataBaseManager();
			
			if (!dataBase.isValid()) {
				returnError(Common.ErrorCode.SERVER_ERROR);
			}else {
				switch (operation) {
					case Common.Operation.LOGIN:
						if (dataBase.isUserExist(user_name)) {
							returnSuccess();
						}else {
							returnError(Common.ErrorCode.NON_EXIST_USER);
						}
						break;
					case Common.Operation.REGISTER:
						if (dataBase.isUserExist(user_name)) {
							returnError(Common.ErrorCode.NAME_OCCUPIED);
						}else if(dataBase.addUser(user_name)){
							returnSuccess();
						}else {
							returnError(Common.ErrorCode.SERVER_ERROR);
						}
						break;
					case Common.Operation.UPLOAD:
						int packet_num = msgInJsonObject.getInt(Common.PacketParams.PACKET_NUM);
						JSONArray packets = msgInJsonObject.getJSONArray(Common.PacketParams.PACKETS);
						int i;
						for (i = 0; i < packet_num; i++) {
							try {
								JSONObject object = packets.getJSONObject(i);
								Blob blob = dataBase.getBlob();
								OutputStream os = blob.setBinaryStream(1);
								os.write(object.getString(Common.PacketParams.AIRFLOW_DATA).getBytes());
								os.close();
								if (!dataBase.insertData(user_name, blob))
									break;
								
							} catch (SQLException e) {
								e.printStackTrace();
								break;
							}
						}
						if (i== packet_num) {
							returnSuccess();
						}else {
							returnError(Common.ErrorCode.SERVER_ERROR);
						}
						break;
	
					default:
						break;
				}
			}
			
			dataBase.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				mySocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void returnError(int errorCode) {
		try {
			JSONObject replyJsonObject = new JSONObject();
			replyJsonObject.put(Common.PacketParams.INSTRUCTION, Common.Instruction.REJECTED);
			replyJsonObject.put(Common.PacketParams.ERRORCODE, errorCode);
			OutputStream oStream = mySocket.getOutputStream();
			oStream.write(replyJsonObject.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mySocket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void returnSuccess() {
		try {
			JSONObject replyJsonObject = new JSONObject();
			replyJsonObject.put(Common.PacketParams.INSTRUCTION, Common.Instruction.APPROVED);
			OutputStream oStream = mySocket.getOutputStream();
			oStream.write(replyJsonObject.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mySocket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}