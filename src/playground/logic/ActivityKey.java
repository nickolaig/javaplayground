package playground.logic;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ActivityKey implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4232259649661460924L;
	private String id;


	private String playground;
	
	public ActivityKey() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ActivityKey(String id, String playground) {
		super();
		this.id = id;
		this.playground = playground;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(other == null || this.getClass() != other.getClass()) {
			return false;
		}
		
		ActivityKey otherUserKey = (ActivityKey) other;
		if(!this.getId().equals(otherUserKey.getId())) {
			return false;
		}
		
		return this.getPlayground().equals(otherUserKey.getPlayground()); 
	}
	
	@Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + playground.hashCode();
        return result;
    }
	
	
}
