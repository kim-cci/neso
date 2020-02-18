package org.neso.sample.chapter04;

import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

public class WithdrawOutput {

	@DataField(length = 10, attr = DataType.TEXT	 ,desc = "응답메세지") String responseCode;
}
