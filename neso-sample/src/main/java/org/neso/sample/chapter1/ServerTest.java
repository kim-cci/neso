package org.neso.sample.chapter1;


import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;

import java.util.Arrays;

import org.neso.api.server.handler.support.SingleApiServerHandler;
import org.neso.core.server.Server;

public class ServerTest { 
	
	public static void main(String[] args) throws Exception { 
 
		new Server(new SingleApiServerHandler(2, request -> {
			
			byte[] response = request.getBodyBytes(); 
			Arrays.sort(response); 
			Thread.sleep(1000);
			return response; 
		
		}), 10001).pipeLineLogLevel(LogLevel.INFO).option(ChannelOption.SO_BACKLOG, 100).inoutLogging(false).maxConnections(1).connectionOriented(true).start(); 
	}
}
