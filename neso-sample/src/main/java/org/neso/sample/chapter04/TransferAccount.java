package org.neso.sample.chapter04;

import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

public class TransferAccount {

	@DataField(length = 30, attr = DataType.TEXT	 		,desc = "계좌번호") String accountNo;
	@DataField(length =  3, attr = DataType.ALPHA_NUMERIC	,desc = "은행코드") String bankCode;
	@DataField(length =  9, attr = DataType.NUMERIC  		,desc = "이체금액") int amount;
	@DataField(length = 29, attr = DataType.TEXT 	 	,desc = "받는사람") String ab;
	@DataField(length = 29, attr = DataType.TEXT 	 	,desc = "보내는사람") String ac;
}
