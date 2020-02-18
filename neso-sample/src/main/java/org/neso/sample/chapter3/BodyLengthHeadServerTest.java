package org.neso.sample.chapter3;

import org.neso.api.handler.server.ServerHandler;
import org.neso.api.handler.server.support.BodyLengthHeadServerHandler;
import org.neso.core.server.Server;

public class BodyLengthHeadServerTest {

	public static void main(String[] args) {
		
		SignUpApi signUpApi = new SignUpApi();
		SearchApi searchApi = new SearchApi();
		
		//API식별자는 body의 0 ~ 6 바이트까지에 있음
		int offset = 0;
		int length = 6;
		ServerHandler serverHandler = new BodyLengthHeadServerHandler(2, offset, length);
		serverHandler.registApi("sign", signUpApi);
		serverHandler.registApi("search", searchApi);
		
		Server server = new Server(serverHandler, 10003);
		server.start();
	}
}
