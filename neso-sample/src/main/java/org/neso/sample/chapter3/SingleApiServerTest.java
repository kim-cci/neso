package org.neso.sample.chapter3;

import org.neso.api.handler.server.ServerHandler;
import org.neso.api.handler.server.support.SingleApiServerHandler;
import org.neso.core.server.Server;

public class SingleApiServerTest {

	public static void main(String[] args) {
		
		SignUpApi signUpApi = new SignUpApi();
		
		int headerLength = 2;
		ServerHandler serverHandler = new SingleApiServerHandler(headerLength, signUpApi);
		
		Server server = new Server(serverHandler, 10003);
		server.start();
	}
}
