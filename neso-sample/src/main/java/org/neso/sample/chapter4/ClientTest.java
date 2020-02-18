package org.neso.sample.chapter4;

import org.neso.sample.util.ClientUtil;
 

public class ClientTest { 
	public static void main(String[] args) throws Exception { 
 
		sendJoinApi();
	}
	
	
	public static void sendSearchApi()  throws Exception { 

		StringBuilder sb = new StringBuilder(); 
		sb.append("04search"); //헤더
		sb.append("u001"); //바디
 
		ClientUtil.oneRequestClose(sb.toString(), 4);
	}
	
	
	public static void sendJoinApi()  throws Exception { 

		StringBuilder sb = new StringBuilder(); 
		sb.append("04sign  "); //헤더
		sb.append("john"); //바디
 
		ClientUtil.oneRequestClose(sb.toString(), 4);
	}
}

