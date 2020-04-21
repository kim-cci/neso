package org.neso.sample.chapter1;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
 

public class ClientTest { 
	public static void main(String[] args) throws Exception { 
		
		Socket socket = new Socket(); 
		SocketAddress address = new InetSocketAddress(InetAddress.getByName("localhost"), 10001); 
		socket.connect(address); 
		Thread.sleep(4000);
		
		socket.getOutputStream().write("05".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		socket.getOutputStream().write("A".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		
		socket.getOutputStream().write("B".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		
		socket.getOutputStream().write("C".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		
		socket.getOutputStream().write("D".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		
		socket.getOutputStream().write("E".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		
		byte[] responseBytes = new byte[5]; 
		socket.getInputStream().read(responseBytes); 
		System.out.println(new String(responseBytes)); 
	 
	 
	Thread.sleep(4000);
		
		socket.getOutputStream().write("03".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		socket.getOutputStream().write("F".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(4000);
		
		socket.getOutputStream().write("F".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		
		socket.getOutputStream().write("F".getBytes()); 
		socket.getOutputStream().flush(); 
		Thread.sleep(1000);
		
		
		byte[] responseBytes2 = new byte[5]; 
		socket.getInputStream().read(responseBytes2); 
		System.out.println(new String(responseBytes2)); 
		
		socket.close(); 
	}
}

