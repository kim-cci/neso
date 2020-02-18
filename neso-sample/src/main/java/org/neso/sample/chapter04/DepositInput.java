package org.neso.sample.chapter04;

import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

public class DepositInput {

	@DataField(length = 10, attr = DataType.TEXT	 ,desc = "임금자명") String name;
	@DataField(length = 10, attr = DataType.NUMERIC  ,desc = "입금금액") int amount;
	@DataField(length = 20, attr = DataType.TEXT 	 ,desc = "입금계좌") String accountNo;

}