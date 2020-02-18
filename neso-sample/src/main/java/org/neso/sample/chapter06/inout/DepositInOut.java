package org.neso.sample.chapter06.inout;

import org.neso.bind.DataScope;
import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;
import org.neso.bind.annotation.ObjectField;
import org.neso.sample.chapter06.server.Head;

import lombok.Data;

@Data
public class DepositInOut {

	@ObjectField(desc = "헤더") Head head;
	
	@DataField(length = 10, attr = DataType.TEXT	 ,desc = "임금자명", scope = DataScope.REQUEST) String name;
	@DataField(length = 10, attr = DataType.NUMERIC  ,desc = "입금금액", scope = DataScope.REQUEST) int amount;
	@DataField(length = 20, attr = DataType.TEXT 	 ,desc = "입금계좌", scope = DataScope.REQUEST) String accountNo;
}