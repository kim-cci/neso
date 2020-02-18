package org.neso.sample.chapter04;

import org.neso.bind.util.ByteBindUtils;
import org.neso.core.Api;
import org.neso.core.request.HeadBodyRequest;

public class WithdrawApiHandler implements Api {

	public byte[] handle(HeadBodyRequest request) throws Exception {
		WithdrawInput input = ByteBindUtils.toObject(request.getBodyBytes(), WithdrawInput.class);

		System.out.println("출금자명=" + input.name);
		System.out.println("출금금액=" + input.amount);
		System.out.println("출금계좌=" + input.accountNo);

		WithdrawOutput output = new WithdrawOutput();
		output.responseCode = withdraw(input) ? "Success": "Fail";
		return ByteBindUtils.toBytes(output);
	}
	
	private static boolean withdraw(WithdrawInput input) {
		//입금 처리를 하는 비지니스 로직.....
		return true;
	}
}
