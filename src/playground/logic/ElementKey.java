package playground.logic;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ElementKey implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4439259649661460924L;
	private String id;
	private String playground;
	
	public ElementKey() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ElementKey(String id, String playground) {
		super();
		this.id = id;
		this.playground = playground;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		
		ElementKey otherElementKey = (ElementKey) other;
		if(!this.getId().equals(otherElementKey.getId())) {
			return false;
		}
		
		return this.getPlayground().equals(otherElementKey.getPlayground()); 
	}
	
	@Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + playground.hashCode();
        return result;
    }
	
	
}
