package org.neso.sample.chapter4;

import org.neso.api.server.handler.ServerHandler;
import org.neso.core.server.Server;

public class PaymentServerTest {

	public static void main(String[] args) {
		
		ServerHandler sh = new PaymentServer();
		sh.registApi("pay_request", request -> { //결제 요청 API
			return "OK".getBytes();
		});
		
		sh.registApi("pay_confirm",request -> {	//결제 확정 API
			return "OK".getBytes();
		});
		
		new Server(sh, 10001).maxConnections(1000).start();
	}
}
