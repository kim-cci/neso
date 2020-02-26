package org.neso.api.server.handler.support;

import java.util.Arrays;

import org.neso.api.Api;
import org.neso.api.server.handler.ServerHandlerListenerAdapter;
import org.neso.core.request.HeadRequest;

/**
 * 단일 API이고, Head 필드에 body에 대한 길이값만 가지고 있고 서버 핸들러
 * 
 *  HEAD 3   |   BODY 12
 * [0][1][2]  [b][b][b][b][b][b][b][b][b][b][b][b]
 *             
 * 
 * headLength = 3
 */
public class SingleApiServerHandler extends ServerHandlerListenerAdapter {

	public SingleApiServerHandler(int headLength, Api api) {
		super(headLength);
		registApi("_SINGLE_API", api);
	}
	
	@Override
	public int getBodyLength(HeadRequest request) {
		try {
			return Integer.parseInt(new String(Arrays.copyOfRange(request.getHeadBytes(), 0, getHeadLength())));
		} catch (Exception e) {
			throw new RuntimeException("invalid body length from head", e);
		}
	}
	
	@Override
	protected String getApiKeyFromHead(byte[] head) {
		return "_SINGLE_API";
	}
}
