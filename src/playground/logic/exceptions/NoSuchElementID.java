package playground.logic.exceptions;

public class NoSuchElementID extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1141347984337505685L;

	public NoSuchElementID() {
	}

	public NoSuchElementID(String message) {
		super(message);
	}

	public NoSuchElementID(Throwable cause) {
		super(cause);
	}

	public NoSuchElementID(String message, Throwable cause) {
		super(message, cause);
	}

}
