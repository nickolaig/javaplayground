package playground.plugins;

public class PostMessageResponse {

	private String message;
	private int points;

	public PostMessageResponse() {
	 
	}
	public PostMessageResponse(String message , int points) {
		 
		this.message=message;
		this.points=points;
	
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}




}
