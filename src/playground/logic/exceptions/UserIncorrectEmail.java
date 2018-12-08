package playground.logic.exceptions;

public class UserIncorrectEmail extends Exception{

	private static final long serialVersionUID = 2094461266408815103L;
	
	public UserIncorrectEmail() {
		super();
	}

	public UserIncorrectEmail(String message) {
		super(message);
	}

	public UserIncorrectEmail(String message, Throwable cause) {
		super(message, cause);
	}

	public UserIncorrectEmail(Throwable cause) {
		super(cause);
	}
}
