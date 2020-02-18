package org.neso.sample.chapter03;

import org.apache.commons.lang3.ArrayUtils;
import org.neso.core.request.HeadRequest;
import org.neso.core.support.server.handler.ServerHandlerAdapter;

public class MultiApiBankServerHandler extends ServerHandlerAdapter {
	
	final static int HEAD_LENGTH_10 = 10;
	final static int BODY_LENGTH_40 = 40;

	public MultiApiBankServerHandler() {
		super(HEAD_LENGTH_10);
	}

	@Override
	public int getBodyLength(HeadRequest request) {
		return BODY_LENGTH_40;
	}
	
	@Override
	public String getApiIdFromHead(byte[] head) {
		return new String(ArrayUtils.subarray(head, 0, 10));
	}
}
