package org.neso.api.handler.server.support;

import java.util.Arrays;

import org.neso.api.handler.server.ServerHandlerListenerAdapter;
import org.neso.core.request.HeadRequest;

/**
 * Head 필드에 body에 대한 길이값만 가지고 있고 api식별값은 바디에 가지고 있는 서버 핸들러
 * 
 *  HEAD 3   |   BODY 12
 *  ----------------------------------------------------------------------
 *  바디길이3  |    API ID(12)                      |   데이터 (8)
 *  ----------------------------------------------------------------------
 * [0][2][0]  [S][E][A][R][C][H][A][P][I][ ][ ][ ][b][b][b][b][b][b][b][b]
 * 
 * 
 * headLength = 3
 * apiIdFieldOffsetOnBody -> 0
 * apiIdFieldLengthOnBody -> 12
 */
public class BodyLengthHeadServerHandler extends ServerHandlerListenerAdapter {
	
	final private int apiIdFieldOffsetOnBody;
	final private int apiIdFieldLengthOnBody;
	
	public BodyLengthHeadServerHandler(int headLength, int apiIdFieldOffsetOnBody, int apiIdFieldLengthOnBody) {
		super(headLength);
		this.apiIdFieldOffsetOnBody = apiIdFieldOffsetOnBody;
		this.apiIdFieldLengthOnBody = apiIdFieldLengthOnBody;
	}
	
	@Override
	public int getBodyLength(HeadRequest request) {
		return Integer.parseInt(new String(Arrays.copyOfRange(request.getHeadBytes(), 0, getHeadLength())));
	}
	
	@Override
	public String getApiIdFromBody(byte[] body) {
		return new String(Arrays.copyOfRange(body, apiIdFieldOffsetOnBody, (apiIdFieldOffsetOnBody + apiIdFieldLengthOnBody)), getCharset()).trim();
	}
}