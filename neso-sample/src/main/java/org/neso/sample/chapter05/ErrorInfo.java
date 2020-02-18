package org.neso.sample.chapter05;

import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

public class ErrorInfo {

	@DataField(length = 4, 	attr = DataType.ALPHA_NUMERIC	,desc = "오류코드") String errorCode;
	@DataField(length = 100,attr = DataType.TEXT 			,desc = "오류메세지") String errorMessage;
}
