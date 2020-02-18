package org.neso.sample.spring.model;

import org.neso.core.request.core.bind.DataType;
import org.neso.core.request.core.bind.annotation.DataField;


public class User {

	@DataField(length = 4, attr = DataType.NUMERIC,  desc = "회원번호") int userNo;
	@DataField(length = 4, attr = DataType.ALPHA_NUMERIC,  desc = "나이") int age;
	@DataField(length = 12, attr = DataType.ALPHA_NUMERIC,  desc = "이름") String name;
}
