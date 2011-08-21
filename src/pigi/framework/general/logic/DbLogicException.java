package pigi.framework.general.logic;

import pigi.framework.general.PigiException;

/**
 * Exception thrown by database logic classes
 */
public class DbLogicException extends PigiException {

	/**
     * 
     */
    private static final long serialVersionUID = -4772165582236648465L;

    public DbLogicException() {
		super();
	}

	public DbLogicException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DbLogicException(String arg0) {
		super(arg0);
	}

	public DbLogicException(Throwable arg0) {
		super(arg0);
	}

}
