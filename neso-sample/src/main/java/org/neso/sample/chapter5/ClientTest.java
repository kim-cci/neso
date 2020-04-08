package org.neso.sample.chapter5;

import java.io.IOException;
import java.net.Socket;

import org.neso.sample.chapter5.externe.Bytes;
import org.neso.sample.util.ClientUtil;
 

public class ClientTest { 
	
	
	public static void main(String[] args) throws Exception { 
 
		sendPayApi();
	}
	
	
	public static void sendGetPayAccountApi()  throws Exception {
		StringBuilder sb = new StringBuilder(); 
		sb.append("API_01"); 	//헤더 : API 식별키 (6바이트)
		sb.append("0010"); 		//헤더 : 본문길이  (4바이트)
		sb.append("CORP01"); 	//헤더 : 업체코드  (6바이트)
		sb.append("    "); 		//헤더 :응답코드  (4바이트)
		sb.append("ACC1234567");//바디: 계좌번호(10바이트)
		receive(ClientUtil.send(10000, sb.toString().getBytes()));
	}
	
	
	public static void sendPayApi()  throws Exception { 

		StringBuilder sb = new StringBuilder(); 
		sb.append("API_02"); 	//헤더 : API 식별키 (6바이트)
		sb.append("0030"); 		//헤더 : 본문길이  (4바이트)
		sb.append("CORP01"); 	//헤더 : 업체코드  (6바이트)
		sb.append("    "); 		//헤더 :응답코드  (4바이트)
		sb.append("ACC1234567");//바디: 계좌번호(10바이트)
		sb.append("T_NO_00001");//바디: 거래번호(10바이트)
		sb.append("0000100000");//바디: 결제금액(10바이트)
		receive(ClientUtil.send(10000,sb.toString().getBytes()));
	}
	
	private static void receive(Socket socket) throws IOException {
		
		byte[] head = new byte[20];
		socket.getInputStream().read(head);
		System.out.println("HEADER(20) : [" + new String(head) + "]");
		
		int bodyLength = Bytes.toNumeric(head, 6, 10);
		byte[] body= new byte[bodyLength];
		socket.getInputStream().read(body);
		System.out.println("BODY(" + bodyLength + ") : [" + new String(body) + "]");
		socket.close();
	}
}

