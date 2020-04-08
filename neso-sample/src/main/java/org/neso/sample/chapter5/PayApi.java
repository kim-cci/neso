package org.neso.sample.chapter5;

import org.neso.api.Api;
import org.neso.core.request.HeadBodyRequest;
import org.neso.sample.chapter5.externe.Bo;
import org.neso.sample.chapter5.externe.Bytes;

public class PayApi implements Api{
	
	private Bo bo = new Bo();
	
	@Override
	public byte[] handle(HeadBodyRequest req) throws Exception {
		
		String corpCode = req.getAttribute(PayServerHandler.ATTR_CORP_CODE);

		String accountNo = Bytes.toText(req.getBodyBytes(), 0, 10);
		String tradeNo = Bytes.toText(req.getBodyBytes(), 10, 20);
		int payAmt = Bytes.toNumeric(req.getBodyBytes(), 20, 30);
		
		if (!bo.isExistAccount(corpCode, accountNo)) {
			return ResponseUtils.make("0201", "not exi....".getBytes(), req.getHeadBytes());
		}
		
		int result = bo.pay(corpCode, tradeNo, accountNo, payAmt);
		if (result != 0) {
			return ResponseUtils.make("020" + result, "some error mes..".getBytes(), req.getHeadBytes());
		}
		
		return ResponseUtils.succes(new byte[0], req.getHeadBytes());
	}
}
