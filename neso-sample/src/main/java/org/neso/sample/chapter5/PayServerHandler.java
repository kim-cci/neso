package org.neso.sample.chapter5;

import java.util.Arrays;

import org.neso.api.server.handler.ServerHandlerAdapter;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.HeadRequest;
import org.neso.core.request.Session;
import org.neso.sample.chapter5.externe.Bo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayServerHandler extends ServerHandlerAdapter {
	
	public static final String ATTR_CORP_CODE = "_ATTR_CORP_CODE";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Bo aclBo = new Bo();
	
	@Override
	public int headLength() {
		return 20;
	}
	
	@Override
	public int bodyLength(HeadRequest req) {
		//0 ~ 5까지 6바이트는  API식별자 , 6 ~ 9까지는 본문길이 4바이트 
		byte[] bodyLengthBytes = Arrays.copyOfRange(req.getHeadBytes(), 6, 10);
		return Integer.parseInt(new String(bodyLengthBytes).trim());
	}
	
	@Override
	protected String apiKeyFromHead(byte[] head) {
		//0 ~ 5까지  6바이트는  API식별자
		byte[] apiKeyBytes = Arrays.copyOfRange(head, 0, 6);
		return new String(apiKeyBytes).trim();
	}
	
	@Override
	public byte[] preApiExecute(Session session, HeadBodyRequest req) {
		
		byte[] corpCodeBytes = Arrays.copyOfRange(req.getHeadBytes(), 10, 16);
		String corpCode = new String(corpCodeBytes);

		if (!aclBo.isValidIp(corpCode, session.getRemoteAddr())) { 
			//허용 IP가 아니라면 오류 응답, API 실행 X
			return ResponseUtils.make("0403", "not allowed".getBytes(), req.getHeadBytes());
		}
		
		req.addAttribute(ATTR_CORP_CODE, corpCode);
		return null;
	}
	
	
	@Override
	protected byte[] exceptionCaughtDoRequest(Session ses, HeadBodyRequest req, Throwable th) {
		logger.error("API 오류 발생 + " + Arrays.toString(req.getHeadBytes()), th);
		return ResponseUtils.make("0500",  "server error".getBytes(), req.getHeadBytes());
	}
}