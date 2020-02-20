package org.neso.sample.chapter06.api;

import org.neso.api.support.BindingApi;
import org.neso.core.request.HeadBodyRequest;
import org.neso.sample.chapter06.inout.DepositInOut;


public class DepositApiHandler extends BindingApi<DepositInOut, DepositInOut>{

	
	@Override
	protected DepositInOut handle(HeadBodyRequest request, DepositInOut input) throws Exception {
		
		return input;
	}
}