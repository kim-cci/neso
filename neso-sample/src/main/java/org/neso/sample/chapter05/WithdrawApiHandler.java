package org.neso.sample.chapter05;

import org.neso.core.request.HeadBodyRequest;
import org.neso.core.support.api.BindingApiHandler;
import org.neso.sample.chapter05.WithdrawInput;

public class WithdrawApiHandler extends BindingApiHandler<WithdrawInput, WithdrawInput> {

	@Override
	protected WithdrawInput handle(HeadBodyRequest request, WithdrawInput input) throws Exception {
		
		System.out.println("출금자명=" + input.name);
		System.out.println("출금금액=" + input.amount);
		System.out.println("출금계좌=" + input.accountNo);
		
		withdraw(input);
		return input;
	}
	
	private static boolean withdraw(WithdrawInput input) {
		//입금 처리를 하는 비지니스 로직.....
		return true;
	}
}
