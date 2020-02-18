package org.neso.bind.exception;

@SuppressWarnings("serial")
public class DataBindException extends RuntimeException {

    String field = "";
    String causeStr = "";
    
    byte[] originData = {};

    public DataBindException(Throwable th) {
    	super(th);
    }
    
    public DataBindException(String field, String cause) {
        super("field=[" + field + "], cuase=" + cause);
        
        this.field = field;
        this.causeStr = cause;
    }
    
    public DataBindException(String field, String cause, Throwable th) {
        super("field=[" + field + "], cuase=" + cause, th);
        
        this.field = field;
        this.causeStr = cause;
    }
    
    public DataBindException(String field, String cause, byte[] origin) {
        super("field=[" + field + "], cuase=" + cause + ", byte=[" + new String(origin) + "]"); //encding 귀찮 ㅜㅜ
        
        this.field = field;
        this.causeStr = cause;
        this.originData = origin;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCauseStr() {
        return causeStr;
    }

    public void setCause(String cause) {
        this.causeStr = cause;
    }

    public byte[] getOriginData() {
        return originData;
    }

    public void setOriginData(byte[] originData) {
        this.originData = originData;
    }
}

