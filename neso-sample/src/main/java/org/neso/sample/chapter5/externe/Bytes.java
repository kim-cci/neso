package org.neso.sample.chapter5.externe;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class Bytes {

	public static String toText(byte[] src, int from, int to) {
		return new String(Arrays.copyOfRange(src, from, to)).trim();
	}
	
	public static int toNumeric(byte[] src, int from, int to) {
		return Integer.parseInt(toText(src, from, to));
	}
	
	public static byte[] join(byte[]... arr) {
		if (arr.length == 0) {
			return new byte[0];
		}
		
		if (arr.length == 1) {
			return arr[0];
		}
		
		byte[] ret = arr[0];
		for (int i = 1; i < arr.length; i++) {
			ret = ArrayUtils.addAll(ret, arr[i]);
		}
		return ret;
	}
}
