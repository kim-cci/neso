package org.neso.sample.chapter5;


import java.util.Arrays;

import org.neso.api.server.handler.ServerHandler;
import org.neso.api.server.handler.support.SingleApiServerHandler;
import org.neso.core.server.Server;

public class ServerTest { 
	
	public static void main(String[] args) throws Exception { 
		
		ServerHandler sh = new SingleApiServerHandler(2, request -> {
			
			byte[] response = request.getBodyBytes(); 
			Thread.sleep(1000);
			Arrays.sort(response); 
			return response; 
		
		});
		
		new Server(sh, 10001).
		connectionOriented().
		requestTaskExecutorPoolSize(1).start();
	}
}
