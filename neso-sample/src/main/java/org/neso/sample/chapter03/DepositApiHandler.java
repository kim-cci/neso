package org.neso.sample.chapter03;

import org.apache.commons.lang3.ArrayUtils;
import org.neso.core.Api;
import org.neso.core.request.HeadBodyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositApiHandler implements Api {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public byte[] handle(HeadBodyRequest request) throws Exception {
		byte[] bodyBytes = request.getBodyBytes();
		
		String name = new String(ArrayUtils.subarray(bodyBytes, 0, 10));
		String amount = new String(ArrayUtils.subarray(bodyBytes, 10, 20));
		
		logger.debug("입금자명=" + name);
		logger.debug("입금금액=" + amount);
		System.out.println("");
		for (int i = 0; i < 15; i++) {
			Thread.sleep(1000);
			System.out.print(".");
		}
		System.out.println("");
		
		logger.debug("서비스 작업이 완료 되었습니다.");
		return "Success   ".getBytes();
	}
	
}
