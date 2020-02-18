package org.neso.sample.chapter06.inout;

import java.util.List;

import org.neso.bind.DataScope;
import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;
import org.neso.bind.annotation.ListField;
import org.neso.bind.annotation.ObjectField;

import lombok.Data;

@Data
public class TransferInOut {

	@DataField(length = 10, attr = DataType.TEXT ,desc = "이체실행시간") String transferTime;
	@DataField(length = 10, attr = DataType.TEXT ,desc = "이체인증정보") String authKey;
	@DataField(length = 20, attr = DataType.TEXT ,desc = "이체...") String transInfo;
	
	//요청에만 있고 응답에는 미포함 = scope = DataScope.REQUEST
	@ObjectField(desc = "이체자 정보", scope = DataScope.REQUEST) TransferCustomer transferCustomer;
	
	@ListField(desc = "이체 계좌들", listSizeFieldLength = 4) List<TransferAccount> transferAccounts;
	
	
	//응답 추가
	@DataField(scope = DataScope.RESPONSE, length = 10, attr = DataType.TEXT ,desc = "이체결과") String responseCode;
}
