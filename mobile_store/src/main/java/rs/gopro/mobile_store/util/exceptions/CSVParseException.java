package rs.gopro.mobile_store.util.exceptions;

public class CSVParseException extends Exception {
	private static final long serialVersionUID = -3623848494235659168L;
	
	public CSVParseException() {
		super();
	}

	public CSVParseException(String message) {
		super(message);
	}
	
	public CSVParseException(Throwable throwable) {
		super(throwable);
	}
	
	public CSVParseException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
