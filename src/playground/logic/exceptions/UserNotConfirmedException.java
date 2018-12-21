package playground.logic.exceptions;

public class UserNotConfirmedException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4736255061558506221L;

	public UserNotConfirmedException() {
	}

	public UserNotConfirmedException(String message) {
		super(message);
	}

	public UserNotConfirmedException(Throwable cause) {
		super(cause);
	}

	public UserNotConfirmedException(String message, Throwable cause) {
		super(message, cause);
	}
}
