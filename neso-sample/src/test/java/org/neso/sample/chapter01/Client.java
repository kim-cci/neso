package org.neso.sample.chapter01;

import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class Client extends TestCase {

	public void testClient() throws Throwable {
    	Socket socket = new Socket();
	    
		try {
		    InetAddress inteAddress = InetAddress.getByName("localhost");
		    SocketAddress socketAddress = new InetSocketAddress(inteAddress, 10011);

	        socket.connect(socketAddress, 10 * 1000);

			byte[] sendBytes = "HeadHi,this is body.".getBytes(); //총 20바이트
			
	        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream(), 1024);
	        bos.write(sendBytes);
	        bos.flush();
	        
	        
	        byte[] responseBytes = new byte[20];
	        socket.getInputStream().read(responseBytes);
	        
	        System.out.println(new String(responseBytes));
	        
	        assertEquals("HeadHi,this is body.", new String(responseBytes));
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
