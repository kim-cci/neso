package org.neso.sample.chapter04;

import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;

public class School {

	@DataField(length = 10, attr = DataType.TEXT	,desc = "학교명") String schoolName;
	@DataField(length = 10, attr = DataType.TEXT	,desc = "위치") String posi;
}
