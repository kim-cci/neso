package org.neso.sample.chapter5.externe;

public class Dao {

	public Account selectAccount(String corpCode, String accountNo) {
		Account account = new Account();
		account.setOwnerName("KIM MINSU ");
		account.setBalance(150_0000);
		return account;
	}
	
	
	public int updateAccountBalance(String corpCode, String accountNo, int deductAmt) {
		//where 잔액 > payAmt
		return 1;
	}
	
	public int insertTrade(String corpCode, String tradeNo) {
		return 1;
	}

	public int updateTrade(String corpCode, String accountNo, int payAmt, String tradeStatus) {
		return 1;
	}
}
