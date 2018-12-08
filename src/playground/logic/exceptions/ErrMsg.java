package playground.logic.exceptions;

public class ErrMsg {

	private String message;

	public ErrMsg() {
	}

	public ErrMsg(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
