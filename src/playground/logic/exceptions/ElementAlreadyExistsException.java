package playground.logic.exceptions;

public class ElementAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public ElementAlreadyExistsException() {
	}

	public ElementAlreadyExistsException(String message) {
		super(message);
	}

	public ElementAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public ElementAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	

	
}
