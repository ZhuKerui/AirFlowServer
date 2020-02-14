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
		msg.put(Common.PacketParams.USER_NAME, "kerui_2");
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
			System.out.println("成功");
		}else {
			switch (msgInJsonObject.getInt(Common.PacketParams.ERRORCODE)) {
				case Common.ErrorCode.NON_EXIST_USER:
					System.out.println("用户不存在");
					break;
				case Common.ErrorCode.NAME_OCCUPIED:
					System.out.println("用户已注册");
					break;
				case Common.ErrorCode.SERVER_ERROR:
					System.out.println("服务器端错误");
					break;
				default:
					System.out.println("什么鬼");
					break;
			}
		}
		socket.close();
	}

}
