package org.neso.sample.spring.api;

import org.neso.core.request.core.ByteRequest;
import org.neso.core.request.core.support.api.BindingApiHandler;
import org.neso.sample.spring.model.DepositInput;
import org.neso.sample.spring.model.DepositOutput;
import org.neso.sample.spring.server.AmericaBankServerHandler;
import org.neso.sample.spring.server.KoreaBankServerHandler;
import org.neso.spring.annotation.ApiHandlerMapping;

@ApiHandlerMapping(id = "V001A008", desc = "계좌 입금", serverHandlerClz = {KoreaBankServerHandler.class, AmericaBankServerHandler.class})
public class DepositApi extends BindingApiHandler<DepositInput, DepositOutput> {

	@Override
	protected DepositOutput handle(ByteRequest request, DepositInput model) throws Exception {
		System.out.println( "API-LOG" + model.toString());
		
		
		return new DepositOutput();
	}
}
