package playground.logic;

import org.springframework.stereotype.Component;

@Component
public class UserTO {
	
	private String email;
	private String playground;
	private String userName;
	private String avatar;
	private String role;
	private long points;
	private boolean isValidate;
	private boolean isEnabled;
	private int code;
	
	public UserTO() {
		// TODO Auto-generated constructor stub
	}

	public UserTO(String email, String playground, String userName, String avatar, String role) {
		super();
		this.email = email;
		this.playground = playground;
		this.userName = userName;
		this.avatar = avatar;
		this.role = role;
		this.points = 0;
		this.isEnabled = true;
		this.isValidate = false;
	}
	
	public UserTO(UserEntity user) {
		this();
		if(user != null) {
			this.email = user.getUserEmailPlaygroundKey().getEmail();
			this.playground = user.getUserEmailPlaygroundKey().getPlayground();
			this.userName = user.getUserName();
			this.avatar = user.getAvatar();
			this.role = user.getRole();
			this.points = user.getPoints();
			this.isValidate = user.getIsValidate();
			this.code = user.getCode();
			this.isEnabled = user.isEnabled();
		}
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

	public boolean isValidate() {
		return isValidate;
	}

	public void setValidate(boolean isValidate) {
		this.isValidate = isValidate;
	}

	
	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public UserEntity toEntity() {
		UserEntity rv = new UserEntity();
		rv.setUserEmailPlaygroundKey(new UserKey(this.email, this.playground));		
		rv.setAvatar(this.avatar);
		rv.setPoints(this.points);
		rv.setRole(this.role);
		rv.setUserName(this.userName);
		rv.setIsValidate(this.isValidate);
		rv.setCode(this.code);
		rv.setEnabled(this.isEnabled);
		return rv;
	}
	
}
