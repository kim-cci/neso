package org.neso.sample.chapter2;

import org.neso.api.server.handler.ServerHandler;
import org.neso.api.server.handler.support.SingleApiServerHandler;
import org.neso.core.server.Server;


public class ServerTest {

	public static void main(String[] args) throws Exception { 
		
		final int HEAD_FIELD_LENGTH = 2;
		ServerHandler serverHandler = new SingleApiServerHandler(HEAD_FIELD_LENGTH, request -> {
			
			byte[] headerBytes = request.getHeadBytes();
			byte[] bodyBytes = request.getBodyBytes(); 
			
			System.out.println("헤더 문자=" + new String(headerBytes));
			System.out.println("바디 문자=" + new String(bodyBytes));
			
			return "OK".getBytes(); 
		});
		
		new Server(serverHandler, 10002).start(); 
		
	}
}
