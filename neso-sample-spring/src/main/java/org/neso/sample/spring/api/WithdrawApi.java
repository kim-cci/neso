package org.neso.sample.spring.api;

import org.neso.core.request.core.ByteRequest;
import org.neso.core.request.core.support.api.BindingApiHandler;
import org.neso.sample.spring.model.WithdrawInOut;
import org.neso.sample.spring.server.AmericaBankServerHandler;
import org.neso.sample.spring.server.KoreaBankServerHandler;
import org.neso.spring.annotation.ApiHandlerMapping;

@ApiHandlerMapping(id = "V001A009", desc = "계좌 출금", serverHandlerClz = {KoreaBankServerHandler.class, AmericaBankServerHandler.class})
public class WithdrawApi extends BindingApiHandler<WithdrawInOut, WithdrawInOut> {

	@Override
	protected WithdrawInOut handle(ByteRequest request, WithdrawInOut model) throws Exception {
		
		System.out.println("계좌 출급=" + model);
		
		model.setResponseCode("0000");
		return model;
	}
}
