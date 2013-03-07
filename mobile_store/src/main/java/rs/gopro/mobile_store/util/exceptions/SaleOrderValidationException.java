package rs.gopro.mobile_store.util.exceptions;

public class SaleOrderValidationException extends Exception {

	private static final long serialVersionUID = -3016656065071978020L;

	public SaleOrderValidationException() {
	}

	public SaleOrderValidationException(String detailMessage) {
		super(detailMessage);
	}

	public SaleOrderValidationException(Throwable throwable) {
		super(throwable);
	}

	public SaleOrderValidationException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

}
