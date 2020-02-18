package org.neso.sample.chapter06.inout;

import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

/*
 * 이체자 정보
 */
public class TransferCustomer {

	@DataField(length = 20, attr = DataType.TEXT	 		,desc = "이체자명") String name;
	@DataField(length =  1, attr = DataType.ALPHA_NUMERIC	,desc = "이체자성별") String gender;
	@DataField(length =  9, attr = DataType.ALPHA_NUMERIC  	,desc = "이체자등급") String grade;
	@DataField(length =  9, attr = DataType.NUMERIC 	 	,desc = "이체자수수료율") int fee;
	@DataField(length =  9, attr = DataType.NUMERIC 	 	,desc = "이체자한도액") int limit;
	@DataField(length = 20, attr = DataType.TEXT 	 		,desc = "이체자대출여부") String aaaa;
	@DataField(length = 20, attr = DataType.TEXT 			,desc = "이체자담당자") String bbbbb;
	@DataField(length = 20, attr = DataType.TEXT 			,desc = "이체자인증키") String ccccc;
}
