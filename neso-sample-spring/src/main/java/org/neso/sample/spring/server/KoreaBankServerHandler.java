package org.neso.sample.spring.server;

import org.neso.core.request.core.ByteRequest;
import org.neso.core.request.core.support.server.handler.HeadBasedServerHandler;
import org.neso.spring.annotation.MultiApiServerHandlerMapping;
import org.neso.spring.annotation.MultiApiServerHandlerMapping.ServerRunOption;

@MultiApiServerHandlerMapping(
	serverHandlerName = "한국은행 서버", runOptions = @ServerRunOption(port = 10010, readTimeout = 2)
)
public class KoreaBankServerHandler extends HeadBasedServerHandler {
	
	static final int HEAD_SIZE = 12;
	static final int LENGTH_FIELD_OFFSET = 0; 
	static final int LENGTH_FIELD_LENGTH = 4; 
	static final int ID_FIELD_OFFSET = 4; 
	static final int ID_FIELD_LENGTH = 8; 
	
	public KoreaBankServerHandler() {
		super(HEAD_SIZE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, ID_FIELD_OFFSET, ID_FIELD_LENGTH);
	}
	
	public byte[] handleException(ByteRequest request, Throwable exception) {
		// TODO Auto-generated method stub
		return null;
	}
}
