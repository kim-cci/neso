package org.neso.sample.util;

import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientUtil {

	
	public static void oneRequestFixedResClose(int port, String request, int responseLength) throws Exception {
		
		System.out.println("request=" + new String(request));

		Socket socket = send(port, request.getBytes());
		
		byte[] res = new byte[responseLength];
				
		socket.getInputStream().read(res);
		System.out.println("response=" + new String(res));
		socket.close();
	}
	

	public static Socket send(int port, byte[] request)  throws Exception {
		Socket socket = new Socket();
		
		SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("localhost"), port);

		socket.connect(socketAddress, 10 * 1000);
 		
	    BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream(), 1024);
	    bos.write(request);
	    bos.flush();
	   
	    return socket;
	}
}
