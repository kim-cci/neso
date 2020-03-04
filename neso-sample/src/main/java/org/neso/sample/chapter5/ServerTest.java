package org.neso.sample.chapter5;


import java.util.Arrays;

import org.neso.api.server.handler.ServerHandler;
import org.neso.api.server.handler.support.SingleApiServerHandler;
import org.neso.core.server.Server;

public class ServerTest { 
	
	public static void main(String[] args) throws Exception { 
		
		ServerHandler sh = new SingleApiServerHandler(2, request -> {
			
			byte[] response = request.getBodyBytes(); 
			Arrays.sort(response); 
			return response; 
		
		});
		//sh.setRepeatableReceiveRequest(true);
		new Server(sh, 10001).start(); //.readTimeoutMillisOnReadStatus(3000)
	}
}
