package org.neso.sample.spring;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class SocketClientTest {
    static int tt = 0;
    
    private final static String DATA = "0042V001A0090000001INOUT01   10037KIM HYUNHWAK";
    
    public static void main(String[] args) {
        single();
    }
    
    public static void single() {
        SocketClientTest ci = new SocketClientTest();
        ci.simpleSocketClient(DATA);
    }
    
    public static void multi() {
        final SocketClientTest ci = new SocketClientTest();

        for (int i = 0; i < 30; i++) {
            tt = i;
            Thread a = new Thread(new Runnable() {
                int count = 1;
                
                public void run() {
                    while (true) {
                        
                        try {
                             Thread.sleep(5000 - (tt * 100));
                        } catch (Exception e) {
                            
                        }
                        ci.simpleSocketClient(DATA);
                        count++;
                        System.out.println(count);
                    }
                }
            });
            a.start();
        }
    }
 
	public void simpleSocketClient(String sendMessage) {
		
		try {
		    InetAddress inteAddress = InetAddress.getByName("localhost");
		    SocketAddress socketAddress = new InetSocketAddress(inteAddress, 10011);

		    Socket socket = new Socket();
	        socket.connect(socketAddress, 10 * 1000);
	          
		    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "EUC-KR"));
	        bufferedWriter.write(sendMessage);
	        bufferedWriter.flush();

	        byte[] body = new byte[42];
	        socket.getInputStream().read(body);
	        System.out.println("length=" + body.length);
	        System.out.println("response=" + new String(body, "EUC-KR"));
		    socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
