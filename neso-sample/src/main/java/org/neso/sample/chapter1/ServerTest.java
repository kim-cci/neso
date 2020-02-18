package org.neso.sample.chapter1;


import java.util.Arrays;

import org.neso.api.handler.server.support.SingleApiServerHandler;
import org.neso.core.server.Server;

public class ServerTest { 
	
	public static void main(String[] args) throws Exception { 
 
		new Server(new SingleApiServerHandler(2, request -> {
			
			byte[] response = request.getBodyBytes(); 
			Arrays.sort(response); 
			return response; 
		
		}), 10001).start(); 
	}
}
