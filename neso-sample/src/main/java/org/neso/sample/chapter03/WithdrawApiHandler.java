package org.neso.sample.chapter03;

import org.apache.commons.lang3.ArrayUtils;
import org.neso.core.Api;
import org.neso.core.request.HeadBodyRequest;

public class WithdrawApiHandler implements Api {

	public byte[] handle(HeadBodyRequest request) throws Exception {
		byte[] bodyBytes = request.getBodyBytes();
		
		String name = new String(ArrayUtils.subarray(bodyBytes, 0, 10));
		String amount = new String(ArrayUtils.subarray(bodyBytes, 10, 20));
		String account = new String(ArrayUtils.subarray(bodyBytes, 20, 40));
		
		System.out.println("출금자명=" + name);
		System.out.println("출금금액=" + amount);
		System.out.println("출금계좌=" + account);
		
		boolean result = Withdraw(name, amount, account);
		return result ? "Success   ".getBytes() : "Fail      ".getBytes();
	}
	
	private static boolean Withdraw(String name, String amount, String account) {
		//입금 처리를 하는 비지니스 로직.....
		return true;
	}
}
