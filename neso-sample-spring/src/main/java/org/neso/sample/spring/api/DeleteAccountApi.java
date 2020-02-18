package org.neso.sample.spring.api;

import org.neso.core.request.core.ByteRequest;
import org.neso.core.request.core.support.server.handler.api.support.ApiHandlerAdapter;
import org.neso.sample.spring.server.AmericaBankServerHandler;
import org.neso.sample.spring.server.KoreaBankServerHandler;
import org.neso.spring.annotation.ApiHandlerMapping;


@ApiHandlerMapping(id = "V001A007", desc = "계좌 삭제 API", serverHandlerClz = {KoreaBankServerHandler.class, AmericaBankServerHandler.class})
public class DeleteAccountApi extends ApiHandlerAdapter {

	
	public byte[] handle(ByteRequest request) throws Exception {
		System.out.println("계좌 삭제 API들어옴" + new String(request.getAllBytes()));
	    return request.getAllBytes();
	}
}
