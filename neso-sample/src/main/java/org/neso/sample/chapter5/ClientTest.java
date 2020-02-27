package org.neso.sample.chapter5;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
 

public class ClientTest { 
	public static void main(String[] args) throws Exception { 
		
		Socket socket = new Socket(); 
		SocketAddress address = new InetSocketAddress(InetAddress.getByName("localhost"), 10001); 
		socket.connect(address); 
		 
		for (int i = 0; i < 102; i++) {
			StringBuilder sb = new StringBuilder(); 
			sb.append("30"); //헤더(바디길이필드)는 2 바이트, 바디 길이는 30 
			sb.append("AAAAAAFAAAAABAGGZATJAAABAAEAAA1"); //바디길이는 30 바이트 
			socket.getOutputStream().write("30".getBytes()); 
			socket.getOutputStream().flush(); 
			Thread.sleep(2000);
			socket.getOutputStream().write("AAAAAAFAAAAABAGGZATJAAABAAEAAA1".getBytes()); 
			socket.getOutputStream().flush(); 
			byte[] responseBytes = new byte[30]; 

		//	while(socket.getInputStream().read(responseBytes) > -1) {
	//			System.out.print(new String(responseBytes)); 
//			}
			socket.getInputStream().read(responseBytes);
			System.out.println(new String(responseBytes)); 
//			
			 
		}

		socket.close(); 
	}
}

