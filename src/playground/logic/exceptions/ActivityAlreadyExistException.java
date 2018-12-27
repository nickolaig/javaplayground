package playground.logic.exceptions;

public class ActivityAlreadyExistException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActivityAlreadyExistException() {
	}

	public ActivityAlreadyExistException(String message) {
		super(message);
	}

	public ActivityAlreadyExistException(Throwable cause) {
		super(cause);
	}

	public ActivityAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
