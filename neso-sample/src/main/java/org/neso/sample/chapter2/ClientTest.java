package org.neso.sample.chapter2;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress; 
 

public class ClientTest {

	public static void main(String[] args) throws Exception {
		
		Socket socket = new Socket(); 
		SocketAddress address = new InetSocketAddress(InetAddress.getByName("localhost"), 10002); 
		socket.connect(address); 
		
		for (int i = 0; i < 2; i++) {
			StringBuilder sb = new StringBuilder(); 
			sb.append("06"); //헤더(바디길이필드)는 2 바이트, 바디 길이는 30 
			sb.append("123456"); //바디길이는 30 바이트 
			socket.getOutputStream().write(sb.toString().getBytes()); 
			socket.getOutputStream().flush(); 
			
			byte[] responseBytes = new byte[30]; 
			socket.getInputStream().read(responseBytes); 
			System.out.println(new String(responseBytes)); 
		}

	 
		socket.close(); 
	}
}
