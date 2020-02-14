import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.sf.json.JSONObject;

public class TCPClientTest {

	public static void main(String[] args) throws  IOException {
		Socket socket = new Socket("192.168.1.104", Common.Norms.SERVER_PORT);
		OutputStream oStream = socket.getOutputStream();
		JSONObject msg = new JSONObject();
		msg.put(Common.PacketParams.OPERATION, Common.Operation.LOGIN);
		msg.put(Common.PacketParams.USER_NAME, "kerui");
		oStream.write(msg.toString().getBytes());
		socket.shutdownOutput();
		
		InputStream iStream = socket.getInputStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] input_byte = new byte[1024];
		int len = 0;
		while ((len = iStream.read(input_byte)) != -1) {
			buffer.write(input_byte, 0, len);
		}
		socket.shutdownInput();
		String input_txt = buffer.toString();
		JSONObject msgInJsonObject = JSONObject.fromObject(input_txt);
		if (msgInJsonObject.getInt(Common.PacketParams.INSTRUCTION) == Common.Instruction.APPROVED) {
			System.out.println("�ɹ�");
		}else if (msgInJsonObject.getInt(Common.PacketParams.ERRORCODE) == Common.ErrorCode.NON_EXIST_USER) {
			System.out.println("�û�������");
		}else if (msgInJsonObject.getInt(Common.PacketParams.ERRORCODE) == Common.ErrorCode.SERVER_ERROR) {
			System.out.println("�������˴���");
		}else {
			System.out.println("ʲô��");
		}
		socket.close();
	}

}
