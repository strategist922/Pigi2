package pigi.framework.general;

public class PigiException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -9085313644822528646L;

    public PigiException() {
    }

    public PigiException(String message) {
        super(message);
    }

    public PigiException(Throwable cause) {
        super(cause);
    }

    public PigiException(String message, Throwable cause) {
        super(message, cause);
    }

}
