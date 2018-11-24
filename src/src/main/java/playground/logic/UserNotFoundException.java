package src.main.java.playground.logic;

public class UserNotFoundException extends Exception{

	private static final long serialVersionUID = 5165892664487702154L;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}
}
