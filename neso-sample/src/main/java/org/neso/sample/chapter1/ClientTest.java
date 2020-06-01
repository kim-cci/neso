package org.neso.sample.chapter1;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
 

public class ClientTest { 
	public static void main(String[] args) throws Exception { 
		
		for (int i = 0; i< 1; i++) {
			Socket socket = new Socket(); 
			SocketAddress address = new InetSocketAddress(InetAddress.getByName("localhost"), 10001); 
			socket.connect(address); 
			
			socket.getOutputStream().write("05HELLO".getBytes()); 
			socket.getOutputStream().flush(); 
			
		
			
			
//			Socket socket2 = new Socket();
//			socket2.connect(address); 
//			
//			socket2.getOutputStream().write("06HELLO2".getBytes()); 
//			socket2.getOutputStream().flush(); 
			
			byte[] responseBytes1 = new byte[5]; 
			socket.getInputStream().read(responseBytes1); 
			System.out.println(new String(responseBytes1)); 
			
//			byte[] responseBytes2 = new byte[5]; 
//			socket2.getInputStream().read(responseBytes2); 
//			System.out.println(new String(responseBytes2)); 
			
			//socket.close();
//			socket2.close();
		}
		
		
		Thread.sleep(10000);
	}
	
}

