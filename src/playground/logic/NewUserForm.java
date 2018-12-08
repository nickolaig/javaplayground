package playground.logic;

import org.springframework.stereotype.Component;

@Component
public class NewUserForm {
	
	private String email;
	private String userName;
	private String role;
	private String avatar;
	
	public NewUserForm() {
		// TODO Auto-generated constructor stub
	}
	
	public NewUserForm(String email, String userName, String role, String avatar) {
		super();
		this.email = email;
		this.userName = userName;
		this.role = role;
		this.avatar = avatar;
	}



	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
}
