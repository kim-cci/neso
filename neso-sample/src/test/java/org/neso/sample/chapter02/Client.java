package org.neso.sample.chapter02;

import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import junit.framework.TestCase;

/**
 * 
 */
public class Client extends TestCase {

	public void testClient() throws Throwable {
    	Socket socket = new Socket();

		try {
		    InetAddress inteAddress = InetAddress.getByName("localhost");
		    SocketAddress socketAddress = new InetSocketAddress(inteAddress, 10011);

	        socket.connect(socketAddress, 10 * 1000);

	        String head = "Deposit   ";
	        String name = "Kimchulsu ";
	        String amount = "1000      ";
	        String account = "8789-7878-1234567890";
	        
	        String sendData = head  + name + amount + account;
	        
			byte[] sendBytes = sendData.getBytes();
			
	        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream(), 1024);
	        bos.write(sendBytes);
	        bos.flush();
	      
	        //bos.write((name + amount + account).getBytes());
	        //bos.flush();
	        
	        
	        byte[] responseBytes = new byte[55];
	        socket.getInputStream().read(responseBytes);
	        System.out.println("응답결과=" + new String(responseBytes));
	        
	        //Thread.sleep(10000);
	        //System.out.println("10초 경과");
	     
	        //bos.write(sendBytes);
	        //bos.flush();
	        
	        //socket.getInputStream().read(responseBytes);
	        //System.out.println("2차 응답결과=" + new String(responseBytes));
	        
	        
	        int b = 0;
	        while((b = socket.getInputStream().read()) != -1) {
	            System.out.println("!!=>" + b);
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
