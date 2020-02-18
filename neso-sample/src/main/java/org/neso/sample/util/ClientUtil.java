package org.neso.sample.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientUtil {

	
	public static void oneRequestClose(String request, int responseLength) throws Exception {
		createNrequest(request, new byte[responseLength]).close();
	}
	
	public static void onRequestWait(String request, int responseLength) throws Exception {
		Socket socket = createNrequest(request, new byte[responseLength]);
 
		String b = null;
        while((b = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine()) != null) {
        	System.out.println("response=" + b);
        }
	}
	
	
	private static Socket createNrequest(String request, byte[] response)  throws Exception {
		Socket socket = new Socket();
		
		SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 10001);

		socket.connect(socketAddress, 10 * 1000);
 
		System.out.println("request=" + request);
		byte[] sendBytes = request.getBytes();
			
	    BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream(), 1024);
	    bos.write(sendBytes);
	    bos.flush();
	     
	    socket.getInputStream().read(response);
	    System.out.println("response=" + new String(response));
	    return socket;
	}
}
