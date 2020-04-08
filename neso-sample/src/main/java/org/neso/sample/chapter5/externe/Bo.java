package org.neso.sample.chapter5.externe;

public class Bo {

	private Dao dao = new Dao();
	
	public boolean isValidIp(String CorpCode, String ip) {
		return true;
	}
	
	public Account getAccount(String corpCode, String accountNo) {
		return dao.selectAccount(corpCode, accountNo);
	}
	
	
	public boolean isExistAccount(String corpCode, String accountNo) {
		return dao.selectAccount(corpCode, accountNo) != null;
	}
	
	/**
	 * 
	 * @return 0: 정상, 2: 중복거래, 3: 잔액 부족
	 */
	public int pay(String corpCode, String tradeNo, String accountNo, int payAmt) {
		
		try {
			dao.insertTrade(corpCode, tradeNo);
		} catch (Exception e) { //DuplicateKeyExeption
			return 1;
		}
		
		try {
			
			if (dao.updateAccountBalance(corpCode, accountNo, payAmt) == 1) {
				dao.updateTrade(corpCode, accountNo, payAmt, "SUCCUESS");
				return 0;
			} else {
				//잔액 부족
				dao.updateTrade(corpCode, accountNo, payAmt, "OUT");
				return 2;
			}
				 
		} catch (Exception e) {
			dao.updateTrade(corpCode, accountNo, payAmt, "ERR");
			throw e; //시스템 오류는 server핸들러에서 처리
		}
	}
}
