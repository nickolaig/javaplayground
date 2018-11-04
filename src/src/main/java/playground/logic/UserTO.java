package src.main.java.playground.logic;

import org.springframework.stereotype.Component;

@Component
public class UserTO {
	
	private String email;
	private String playground;
	private String userName;
	private String avatar;
	private String role;
	private long points;
	
	public UserTO() {
		// TODO Auto-generated constructor stub
	}

	public UserTO(String email, String playground, String userName, String avatar, String role, long points) {
		super();
		this.email = email;
		this.playground = playground;
		this.userName = userName;
		this.avatar = avatar;
		this.role = role;
		this.points = points;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}
	
	
}
