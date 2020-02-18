package org.neso.sample.chapter3;

import org.neso.api.handler.server.ServerHandler;
import org.neso.api.handler.server.support.HeadBasedServerHandler;
import org.neso.core.server.Server;

public class HeadBasedServerTest {

	public static void main(String[] args) {
		
		ServerHandler serverHandler = new HeadBasedServerHandler(8, 0, 2, 2, 6);
		serverHandler.registApi("sign", new SignUpApi());
		serverHandler.registApi("search", new SearchApi());
		
		Server server = new Server(serverHandler, 10003);
		server.start();
	}
}
