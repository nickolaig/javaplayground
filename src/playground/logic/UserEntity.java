package playground.logic;

import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class UserEntity {
	
	private UserKey userEmailPlaygroundKey;
	private String userName;
	private String avatar;
	private String role;
	private Long points;
	private Integer code;
	private Boolean isValidate;
	private Boolean isEnabled;


	public UserEntity() {
		// TODO Auto-generated constructor stub
	}

	public UserEntity(UserKey userEmailPlaygroundKey, String userName, String avatar, String role, Long points) {
		super();
		this.userEmailPlaygroundKey = userEmailPlaygroundKey;
		this.userName = userName;
		this.avatar = avatar;
		this.role = role;
		this.points = points;
		this.isValidate = false;
		this.isEnabled=true;
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
	public Boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
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
	
	@EmbeddedId
	public UserKey getUserEmailPlaygroundKey() {
		return userEmailPlaygroundKey;
	}

	public void setUserEmailPlaygroundKey(UserKey userEmailPlaygroundKey) {
		this.userEmailPlaygroundKey = userEmailPlaygroundKey;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Boolean getIsValidate() {
		return isValidate;
	}

	public void setIsValidate(Boolean isValidate) {
		this.isValidate = isValidate;
	}

}
