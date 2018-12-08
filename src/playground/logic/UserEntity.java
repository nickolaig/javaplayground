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
		this.code = generateCode();
		this.isValidate = false;
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

	private Integer generateCode() {
		int minRange = 0;
		int maxRange = 9;
		int generatedCode = 0;

		int randomNumLength = ThreadLocalRandom.current().nextInt(minRange, maxRange + 1);
		int randomDigit;
		
		for(int i = 0 ; i < randomNumLength ; i++) {
			randomDigit = ThreadLocalRandom.current().nextInt(minRange, maxRange + 1);
			generatedCode += Math.pow(10, i);
			generatedCode += randomDigit; 
		}
		
		return generatedCode;
	}
}
