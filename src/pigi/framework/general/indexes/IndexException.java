package pigi.framework.general.indexes;

import pigi.framework.general.PigiException;

/**
 * Exception thrown by index  classes
 */
public class IndexException extends PigiException {

	/**
     * 
     */
    private static final long serialVersionUID = -8078291756919634741L;

    public IndexException() {}

	public IndexException(String message, Throwable t) {
		super(message, t);
	}

	public IndexException(String message) {
		super(message);
	}

	public IndexException(Throwable t) {
		super(t);
	}

}
