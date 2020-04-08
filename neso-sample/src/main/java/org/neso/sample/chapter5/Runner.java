package org.neso.sample.chapter5;

import io.netty.handler.logging.LogLevel;

import org.neso.core.server.Server;

public class Runner {

	public static void main(String[] args) {
		
		PayServerHandler serverHandler = new PayServerHandler();
		
		serverHandler.registApi("API_01", new GetPayAccountApi());
		serverHandler.registApi("API_02", new PayApi());
	
		new Server(serverHandler, 10000)
		.pipeLineLogLevel(LogLevel.DEBUG)
		.start();
	}
}
