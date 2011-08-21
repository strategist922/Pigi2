package pigi.framework.general.vo;

import pigi.framework.general.PigiException;

/**
 * Exception thrown by CRUD class
 */
public class CRUDException extends PigiException {

	/**
     * 
     */
    private static final long serialVersionUID = -1873756585531667979L;

    public CRUDException() {
		super();
	}

	public CRUDException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CRUDException(String arg0) {
		super(arg0);
	}

	public CRUDException(Throwable arg0) {
		super(arg0);
	}

}
