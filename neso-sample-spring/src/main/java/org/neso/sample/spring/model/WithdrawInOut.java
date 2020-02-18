package org.neso.sample.spring.model;

import java.util.List;

import org.neso.core.request.core.bind.DataType;
import org.neso.core.request.core.bind.annotation.DataField;
import org.neso.core.request.core.bind.annotation.ListField;

import lombok.Data;

@Data
public class WithdrawInOut {
	
	@DataField(length = 4, attr = DataType.NUMERIC,  desc = "전문길이") int apiLength;
	@DataField(length = 4, attr = DataType.ALPHA_NUMERIC,  desc = "전문버젼") String apiVer;
	@DataField(length = 4, attr = DataType.ALPHA_NUMERIC,  desc = "전문번호") String apiId;
	@DataField(length = 4, attr = DataType.ALPHA_NUMERIC,  desc = "응답코드") String responseCode;
	
	@DataField(length = 3, attr = DataType.ALPHA_NUMERIC,  desc = "은행코드") String bankCode;
	@DataField(length = 5, attr = DataType.ALPHA_NUMERIC,  desc = "계좌종류") String accoutType;
	@ListField(listSizeFieldLength = 2, desc = "사용자") List<User> user;
}
