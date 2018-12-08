package playground.logic.exceptions;

public class InvalidInputException extends Exception {

	private static final long serialVersionUID = -5957903168281208294L;

	public InvalidInputException() {
		super();
		
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public InvalidInputException(String message) {
		super(message);
		
	}

	public InvalidInputException(Throwable cause) {
		super(cause);
	
	}
}
