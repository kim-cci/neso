package org.neso.sample.chapter4.inherit;

import org.neso.api.server.handler.ServerHandlerAdapter;
import org.neso.core.request.HeadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 결제 서버
 */

public class PaymentServerHandler extends ServerHandlerAdapter {

	final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public int headLength() {
		return 10;
	}
	
	@Override
	public int bodyLength(HeadRequest request) {
        String apiKey = new String(request.getHeadBytes());	// 헤더 바이트에는 api식별자만 있음
        return "API_NO_001".equals(apiKey) ? 50 : 100;
	}
	
	@Override
	protected String apiKeyFromHead(byte[] head) {
		return new String(head);
	}
}
