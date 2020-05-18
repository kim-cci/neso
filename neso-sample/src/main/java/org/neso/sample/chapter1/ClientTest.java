package org.neso.sample.chapter1;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
 

public class ClientTest { 
	public static void main(String[] args) throws Exception { 
		
		Socket socket = new Socket(); 
		SocketAddress address = new InetSocketAddress(InetAddress.getByName("localhost"), 10000); 
		socket.connect(address); 
		
		socket.getOutputStream().write("05HELLO".getBytes()); 
		socket.getOutputStream().flush(); 
		
		byte[] responseBytes2 = new byte[5]; 
		socket.getInputStream().read(responseBytes2); 
		System.out.println(new String(responseBytes2)); 
		
		socket.close(); 
	}
}

