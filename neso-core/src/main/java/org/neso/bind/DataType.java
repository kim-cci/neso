package org.neso.bind;

import org.neso.bind.util.ByteBindUtils;

/**
 * {@link ByteBindUtils}
 * <br>
 * NUMERIC --> int, long<br>
 * [0][0][0][0][1]  --> int 1 or long 1l  -->  [0][0][0][0][1] <br>
 * <br>
 * <br>
 * ALPHA_NUMERIC --> boolean, String, enum<br>
 * [ ][ ][ ][ ][1]  -->  String "&nbsp;&nbsp;&nbsp;&nbsp;1"  -->  [ ][ ][ ][ ][1] <br> 
 * [ ][ ][ ][ ][ ]  -->  String "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"  -->  [ ][ ][ ][ ][ ] <br>
 * [ ][ ][ ][ ][Y]  -->  boolean error<br>
 * [Y]  -->  boolean true --> [Y]<br>
 * <br>
 * <br>
 * TEXT --> boolean, String, enum<br>
 * [1][ ][ ][ ][ ]  -->          String "1"      -->  [1][ ][ ][ ][ ] <br>  
 * [ ][ ][ ][ ][ ]  -->          String ""       -->  [ ][ ][ ][ ][ ] <br>  
 * [y][ ][ ][ ][ ]  -->          boolean true    -->  [Y][ ][ ][ ][ ] <br>         
 */
public enum DataType {
    NUMERIC('0', true), //숫자타입이며 공백은 0으로 채운다
    ALPHA_NUMERIC(' ', true), //alpha-numeric, 숫자 + 문자로  오른쪽 정렬하고 공백은 ' '로 채운다
    TEXT(' ', false); // 문자타입이며 오른쪽 왼쪽정렬 하고 공백은 ' '로 채운다
    
    private byte fillChar;
    private boolean rightAlign;
    
    private DataType(char fillChar, boolean rightAlign) {
    	this.fillChar = (byte) fillChar;
    	this.rightAlign = rightAlign;
    }

    public byte getFillChar() {
    	return fillChar;
    }
    
    public boolean isRightAlign() {
    	return rightAlign;
    }
}
