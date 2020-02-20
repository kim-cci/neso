package org.neso.sample.chapter06.api;

import org.neso.api.support.BindingApi;
import org.neso.core.request.HeadBodyRequest;
import org.neso.sample.chapter06.inout.WithdrawInOut;

public class WithdrawApiHandler extends BindingApi<WithdrawInOut, WithdrawInOut> {

	@Override
	protected WithdrawInOut handle(HeadBodyRequest request, WithdrawInOut input) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
