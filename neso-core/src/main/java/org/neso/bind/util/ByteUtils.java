package org.neso.bind.util;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.neso.bind.DataType;


public class ByteUtils {
    
    public final static Charset DEFAULT_CHARSET = Charset.forName("EUC-KR");
    
    public static byte[] toBytes(String src, int size, DataType type) {
        return toBytes(src, size, DEFAULT_CHARSET, type);
    }
    
    public static byte[] toBytes(String src, int size, Charset charSet, DataType type) {
    	return toBytes(src, charSet, size, type.getFillChar(), type.isRightAlign());
    }
    
    /**
     * 
     * @param src 대상 문자열
     * @param charSet 케릭터셋
     * @param size 배열 size
     * @param fill 채울 문자
     * @param rightAlign
     * @return
     */
    private static byte[] toBytes(String src, Charset charSet, int size, byte fill, boolean rightAlign) {

        if (src == null) {
            src = "";
        }
        byte[] rv = src.getBytes(charSet);
        
        if (rv.length > size) {
            byte[] work = new byte[size];
            System.arraycopy(rv, 0, work, 0, work.length);
            rv = work;
        } else if (rv.length < size) {
            byte[] work = new byte[size];
            if (rightAlign) {
                System.arraycopy(rv, 0, work, size - rv.length, rv.length);
                for (int i = size - rv.length - 1; i >= 0; i--) {
                    work[i] = fill;
                }
            } else {
                System.arraycopy(rv, 0, work, 0, rv.length);
                for (int i = rv.length; i < size; i++) {
                    work[i] = fill;
                }
            }
            rv = work;
        }
        return rv;
    }
    
    public static byte[] newEmptyArray(int length) {
        byte[] ret = new byte[length];
        byte fillChar = ' ';
        Arrays.fill(ret, fillChar);
        return ret;
    }
    
    public static byte[] insertAt(byte[] origin, int offset, byte[] toInsertArr) {
    	if ((offset + toInsertArr.length) > origin.length) {
    		throw new ArrayIndexOutOfBoundsException("not enough origin arr length to insert");
    	}
    	byte[] response = new byte[0];
    	response = ArrayUtils.addAll(response, ArrayUtils.subarray(origin, 0, offset));
    	response = ArrayUtils.addAll(response, toInsertArr);
    	response = ArrayUtils.addAll(response, ArrayUtils.subarray(origin, response.length, origin.length));
		return response;
    }
}
