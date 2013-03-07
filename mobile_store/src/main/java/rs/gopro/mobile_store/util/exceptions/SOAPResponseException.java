package rs.gopro.mobile_store.util.exceptions;

public class SOAPResponseException extends Exception {
	
	private static final long serialVersionUID = 332952461007454891L;

	public SOAPResponseException() {
		super();
	}

	public SOAPResponseException(String detailMessage) {
		super(detailMessage);
	}

	public SOAPResponseException(Throwable throwable) {
		super(throwable);
	}

	public SOAPResponseException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
