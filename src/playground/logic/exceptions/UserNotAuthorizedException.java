package playground.logic.exceptions;

public class UserNotAuthorizedException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6292349481515263253L;

	public UserNotAuthorizedException() {
	}

	public UserNotAuthorizedException(String message) {
		super(message);
	}

	public UserNotAuthorizedException(Throwable cause) {
		super(cause);
	}

	public UserNotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}
}
