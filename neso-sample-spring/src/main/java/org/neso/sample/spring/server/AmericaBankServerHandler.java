package org.neso.sample.spring.server;

import org.neso.core.request.core.ByteRequest;
import org.neso.core.request.core.support.server.handler.HeadOnlyBodyLengthServerHandler;
import org.neso.spring.annotation.MultiApiServerHandlerMapping;
import org.neso.spring.annotation.MultiApiServerHandlerMapping.ServerRunOption;

@MultiApiServerHandlerMapping(
	serverHandlerName = "미국은행 서버", runOptions = @ServerRunOption(port = 10011, readTimeout = 10)
)
public class AmericaBankServerHandler extends HeadOnlyBodyLengthServerHandler {
	
	static final int HEAD_SIZE = 4;
	static final int API_ID_FIELD_OFFSET = 4;
	static final int API_ID_FIELD_LENGTH = 8; 
	
	public AmericaBankServerHandler() {
		super(HEAD_SIZE, API_ID_FIELD_OFFSET, API_ID_FIELD_LENGTH);
	}

	public byte[] handleException(ByteRequest request, Throwable exception) {
		// TODO Auto-generated method stub
		return null;
	}
}
