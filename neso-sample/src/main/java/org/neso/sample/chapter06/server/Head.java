package org.neso.sample.chapter06.server;

import org.neso.bind.DataScope;
import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

import lombok.Data;

@Data
public class Head {

	@DataField(length = 10, attr = DataType.TEXT	 		,desc = "전문번호") String apiId;
	@DataField(length = 4, 	attr = DataType.NUMERIC  		,desc = "본문길이") int bodyLength;
	@DataField(length = 8, 	attr = DataType.ALPHA_NUMERIC 	,desc = "요청일자") String requestYYYYMMDD;
	@DataField(length = 8, 	attr = DataType.ALPHA_NUMERIC 	,desc = "업체코드") String companyCode;
	@DataField(length = 20, attr = DataType.TEXT 			,desc = "업체인증키") String companyKey;
	
	@DataField(length = 4,	attr = DataType.TEXT ,desc = "응답코드",  scope = DataScope.RESPONSE) String responseCode;
	@DataField(length = 100,attr = DataType.TEXT ,desc = "응답메세지",scope = DataScope.RESPONSE) String responseMessage;
}
