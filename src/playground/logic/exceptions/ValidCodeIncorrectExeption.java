package playground.logic.exceptions;

public class ValidCodeIncorrectExeption extends Exception {

	private static final long serialVersionUID = 8238820717796714099L;

	public ValidCodeIncorrectExeption() {
		super();
		
	}

	public ValidCodeIncorrectExeption(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ValidCodeIncorrectExeption(String message) {
		super(message);
	}

	public ValidCodeIncorrectExeption(Throwable cause) {
		super(cause);
		
	}
}
