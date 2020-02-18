package org.neso.sample.chapter04;

import org.neso.bind.util.ByteBindUtils;
import org.neso.core.Api;
import org.neso.core.request.HeadBodyRequest;

public class TransferApiHandler implements Api {

	public byte[] handle(HeadBodyRequest request) throws Exception {
		TransferInput input = ByteBindUtils.toObject(request.getBodyBytes(), TransferInput.class);

		System.out.println("이체실행시간=" + input.transferTime);
		System.out.println("이체자의 등급=" + input.transferCustomer.grade);
		System.out.println("이체계좌 숫자=" + input.transferAccounts.size());

		
		// 비지니스 로직
		boolean result = true;
		
		input.responseCode = result ? "Success": "Fail";
		return ByteBindUtils.toBytes(input);
	}

}
