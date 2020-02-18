package org.neso.sample.chapter06.runner;

import org.neso.core.support.server.ConnectionlessServer;
import org.neso.sample.chapter06.api.DepositApiHandler;
import org.neso.sample.chapter06.api.TransferApiHandler;
import org.neso.sample.chapter06.api.WithdrawApiHandler;
import org.neso.sample.chapter06.server.BankServerHandler;

public class Runner {

	public static void main(String[] args) {
		
//		BankServerHandler multiAPiBankServerHandler = new BankServerHandler();
//		multiAPiBankServerHandler.setServerHandlerName("멀티 API은행 서버");
//		multiAPiBankServerHandler.addApiHandler("1101", new DepositApiHandler());
//		multiAPiBankServerHandler.addApiHandler("2101", new WithdrawApiHandler());
//		multiAPiBankServerHandler.addApiHandler("3101", new TransferApiHandler());
//		
//		ConnectionlessServer server = new ConnectionlessServer(multiAPiBankServerHandler, 10011);
//		server.setRunMode(RunMode.NEW_THREAD);
//		server.start(); //소켓 서버를 가동한다.
	}
}