package pigi.framework.general.views;

import pigi.framework.general.PigiException;

/**
 * Exception thrown by view factory class
 */
public class ViewFactoryException extends PigiException {

	/**
     * 
     */
    private static final long serialVersionUID = 296290227353280582L;

    public ViewFactoryException() {
		super();
	}

	public ViewFactoryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ViewFactoryException(String arg0) {
		super(arg0);
	}

	public ViewFactoryException(Throwable arg0) {
		super(arg0);
	}

}
