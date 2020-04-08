package org.neso.sample.chapter5;

import org.apache.commons.lang3.ArrayUtils;
import org.neso.api.Api;
import org.neso.core.request.HeadBodyRequest;
import org.neso.sample.chapter5.externe.Account;
import org.neso.sample.chapter5.externe.Bo;

public class GetPayAccountApi implements Api{

	private Bo bo = new Bo();
	
	@Override
	public byte[] handle(HeadBodyRequest req) throws Exception {
		
		String corpCode = req.getAttribute(PayServerHandler.ATTR_CORP_CODE);
		
		byte[] payAccountNoBytes = req.getBodyBytes();
		String payAccountNo = new String(payAccountNoBytes).trim();
		
		//업체코드(헤더)와 계좌번호(본문)로 계좌를 조회핸다.
		Account account = bo.getAccount(corpCode, payAccountNo);
		if (account == null) {
			ResponseUtils.make("0010", "invalid accountNo".getBytes(), req.getHeadBytes());
		}
		
		byte[] ownerNameBytes = account.getOwnerName().getBytes();
		byte[] balanceBytes = String.format("%010d", account.getBalance()).getBytes();
		byte[] responseBytes = ArrayUtils.addAll(ownerNameBytes, balanceBytes);
		
		return ResponseUtils.succes(responseBytes, req.getHeadBytes());
	}
}
