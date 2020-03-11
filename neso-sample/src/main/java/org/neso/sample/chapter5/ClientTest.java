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
		 
		for (int i = 0; i < 1; i++) {
			Thread.sleep(2000);
			socket.getOutputStream().write("3".getBytes()); 
			socket.getOutputStream().flush(); 
			Thread.sleep(2000);
			socket.getOutputStream().write("0".getBytes()); 
			socket.getOutputStream().flush(); 
			
			Thread.sleep(2000);
			socket.getOutputStream().write("AAAAAAFAA35ABAGGZATJ35ABAAEAAk".getBytes()); 
			socket.getOutputStream().flush(); 
			
		
			
//			socket.getOutputStream().write("30".getBytes()); 
//			socket.getOutputStream().flush(); 
//			
//			socket.getOutputStream().write("AAAAAAFAA35ABAGGZATJ35ABAAEAAk".getBytes()); 
//			socket.getOutputStream().flush(); 
			
			byte[] responseBytes = new byte[1]; 
			
			for (int j = 0; j < 60; j++) {
				socket.getInputStream().read(responseBytes);
				System.out.print(new String(responseBytes)); 
			}
//			while(socket.getInputStream().read(responseBytes) > -1) {
//				System.out.print(new String(responseBytes)); 
//			}
//			socket.getInputStream().read(responseBytes);
//			System.out.println(new String(responseBytes)); 
//			
			  
		}
		
		socket.close(); 
	}
}

