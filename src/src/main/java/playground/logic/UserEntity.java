package src.main.java.playground.logic;

import org.springframework.stereotype.Component;

@Component
public class UserEntity {
	
	private String email;
	private String playground;
	private String userName;
	private String avatar;
	private String role;
	private Long points;
	
	public UserEntity() {
		// TODO Auto-generated constructor stub
	}

	public UserEntity(String email, String playground, String userName, String avatar, String role, Long points) {
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

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}
	
	
}
