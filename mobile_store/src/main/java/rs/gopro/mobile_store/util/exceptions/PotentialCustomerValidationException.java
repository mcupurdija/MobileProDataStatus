package rs.gopro.mobile_store.util.exceptions;

public class PotentialCustomerValidationException extends Exception {

	private static final long serialVersionUID = 8517854930125581727L;

	public PotentialCustomerValidationException() {
	}

	public PotentialCustomerValidationException(String detailMessage) {
		super(detailMessage);
	}

	public PotentialCustomerValidationException(Throwable throwable) {
		super(throwable);
	}

	public PotentialCustomerValidationException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

}
