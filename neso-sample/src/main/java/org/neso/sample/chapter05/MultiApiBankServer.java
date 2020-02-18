package org.neso.sample.chapter05;

import org.neso.core.support.server.ConnectionlessServer;

public class MultiApiBankServer {

	public static void main(String[] args) throws Exception {
		
		MultiApiBankServerHandler multiAPiBankServerHandler = new MultiApiBankServerHandler();
		multiAPiBankServerHandler.addApi("Deposit", new DepositApiHandler());
		multiAPiBankServerHandler.addApi("Withdraw", new WithdrawApiHandler());
		
		ConnectionlessServer server = new ConnectionlessServer(multiAPiBankServerHandler, 10011);
		server.start();
	}
}