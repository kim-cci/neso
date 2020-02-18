package org.neso.sample.chapter05;

import org.neso.core.request.HeadBodyRequest;
import org.neso.core.support.api.BindingApiHandler;
import org.neso.sample.chapter05.DepositInput;

public class DepositApiHandler extends BindingApiHandler<DepositInput, DepositInput> {

	@Override
	protected DepositInput handle(HeadBodyRequest request, DepositInput input) throws Exception {
	
		deposit(input);
		
		return input;
	}
	
	private static boolean deposit(DepositInput input) {
		//입금 처리를 하는 비지니스 로직.....
		return true;
	}
}
