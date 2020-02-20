package org.neso.sample.chapter04;

import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

public class Head {

	@DataField(length = 10, attr = DataType.TEXT	 		,desc = "전문번호") String apiId;
	@DataField(length = 4, 	attr = DataType.NUMERIC  		,desc = "본문길이") int bodyLength;
	@DataField(length = 8, 	attr = DataType.ALPHA_NUMERIC 	,desc = "요청일자") String requestDay;
	@DataField(length = 8, 	attr = DataType.ALPHA_NUMERIC 	,desc = "업체코드") String companyCode;
}
