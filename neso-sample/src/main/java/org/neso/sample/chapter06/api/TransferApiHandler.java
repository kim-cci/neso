package org.neso.sample.chapter06.api;

import org.neso.core.request.HeadBodyRequest;
import org.neso.core.support.api.BindingApiHandler;
import org.neso.sample.chapter06.inout.TransferInOut;

public class TransferApiHandler extends BindingApiHandler<TransferInOut, TransferInOut> {

	@Override
	protected TransferInOut handle(HeadBodyRequest request, TransferInOut input) throws Exception {
		System.out.println("이체실행시간=" + input.getTransferTime());
		return null;
	}

}
