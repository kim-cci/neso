package org.neso.sample.chapter4;

import org.neso.api.server.handler.ServerHandlerAdapter;
import org.neso.core.request.HeadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 결제 서버
 */
public class PaymentServer extends ServerHandlerAdapter {

	final Logger logSystem = LoggerFactory.getLogger(this.getClass());
	
	private static final int HEAD_LENGTH = 6;
	
	public PaymentServer() {
		super(HEAD_LENGTH);
	}
	
	@Override
	public int getBodyLength(HeadRequest request) {
        String apiKey = new String(request.getHeadBytes());
        
        if ("API_01".equals(apiKey)) { //01 api면 본문은 50
        	return 50;
        } else if ("API_02".equals(apiKey)) { //02 api면 본문 100
        	return 100;
        } else {
        	return 0;
        } 
	}
	
	@Override
	protected String getApiKeyFromHead(byte[] head) {
		return new String(head);
	}
}
