package org.neso.sample.chapter05;

import org.neso.bind.DataScope;
import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

public class WithdrawInput {

	@DataField(length = 10, attr = DataType.TEXT	 ,desc = "출금자명", required = false) String name;
	@DataField(length = 10, attr = DataType.NUMERIC  ,desc = "출금금액", initReqValue = "10") int amount;
	@DataField(length = 20, attr = DataType.TEXT 	 ,desc = "출금계좌", scope = DataScope.REQUEST) String accountNo;
}
