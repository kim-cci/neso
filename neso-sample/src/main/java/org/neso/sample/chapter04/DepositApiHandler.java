package org.neso.sample.chapter04;

import org.neso.bind.util.ByteBindUtils;
import org.neso.core.Api;
import org.neso.core.request.HeadBodyRequest;

public class DepositApiHandler implements Api {

	public byte[] handle(HeadBodyRequest request) throws Exception {

		DepositInput input = ByteBindUtils.toObject(request.getBodyBytes(), DepositInput.class);

		System.out.println("입금자명=" + input.name);
		System.out.println("입금금액=" + input.amount);
		System.out.println("깁금계좌=" + input.accountNo);
		
		return deposit(input) ? "Success   ".getBytes() : "Fail      ".getBytes();
	}
	
	private static boolean deposit(DepositInput input) {
		//입금 처리를 하는 비지니스 로직.....
		return true;
	}
}