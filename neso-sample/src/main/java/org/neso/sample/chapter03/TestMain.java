package org.neso.sample.chapter03;

import org.neso.core.support.server.ConnectionlessServer;
import org.neso.core.support.server.handler.HeadBasedServerHandler;

public class TestMain {

	public static void main(String[] args) throws Exception {
		
		//MultiApiBankServerHandler multiAPiBankServerHandler = new MultiApiBankServerHandler();

		HeadBasedServerHandler multiAPiBankServerHandler = new HeadBasedServerHandler(10, 1, 2, 0, 10);
		multiAPiBankServerHandler.
		attachListnerPostApiExecute((req, reponse) -> {
			return new byte[0];
		}).
		attachListnerPreApiExecute((req) -> {
			return new byte[0];
		});
		
		
		multiAPiBankServerHandler.addApi("Deposit", new DepositApiHandler());
		multiAPiBankServerHandler.addApi("Withdraw", new WithdrawApiHandler());
		
		
		new ConnectionlessServer(multiAPiBankServerHandler, 10011).maxConnections(1).maxTaskThreads(1).start();
	}
}