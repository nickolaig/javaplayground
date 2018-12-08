package playground.logic;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class UserKey implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4439259649661460924L;
	private String email;
	private String playground;
	
	public UserKey() {
		// TODO Auto-generated constructor stub
	}
	
	
	public UserKey(String email, String playground) {
		super();
		this.email = email;
		this.playground = playground;
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
	
	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(other == null || this.getClass() != other.getClass()) {
			return false;
		}
		
		UserKey otherUserKey = (UserKey) other;
		if(!this.getEmail().equals(otherUserKey.getEmail())) {
			return false;
		}
		
		return this.getPlayground().equals(otherUserKey.getPlayground()); 
	}
	
	@Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + playground.hashCode();
        return result;
    }
	
	
}
