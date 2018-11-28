package src.main.java.playground.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ElementEntity {
	
	
	private String playground;
	private String id;
	private Location location;
	private String name;
	private Date creationDate;
	private Date expirationDate;
	private String type;
	private Map<String, Object> attributes=new HashMap<>();
	private String creatorPlayground;
	private String creatorEmail;
	
	
	
	public ElementEntity() {
		
	}
	public ElementEntity(String id) {
		setPlayground("Test");
		this.id=id;

		attributes.put("playground", this.playground);
		attributes.put("id", this.id);
	}
	
	public ElementEntity(String playground, String id, Location location, String name, Date creationDate,
			Date expirationDate, String type, Map<String, Object> attributes, String creatorPlayground,
			String creatorEmail) {
		super();
		this.playground = playground;
		this.id = id;
		this.location = location;
		this.name = name;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.type = type;
		this.attributes = attributes;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;

	}

	
	public String getPlayground() {
		return playground;
	}
	public void setPlayground(String playground) {
		this.playground = playground;
		this.attributes.put("playground", this.playground);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
		this.attributes.put("id", this.id);
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
		this.attributes.put("location", this.location);

	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		this.attributes.put("name", this.name);
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		this.attributes.put("creationDate", this.creationDate);
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
		this.attributes.put("expirationDate", this.expirationDate);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		this.attributes.put("type", this.type);
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public String getCreatorPlayground() {
		return creatorPlayground;
	}
	public void setCreatorPlayground(String creatorPlayground) {
		this.creatorPlayground = creatorPlayground;
		this.attributes.put("creatorPlayground", this.creatorPlayground);
	}
	public String getCreatorEmail() {
		return creatorEmail;
	}
	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
		this.attributes.put("creatorEmail", this.creatorEmail);
	}
	
	public ElementTO toElementTO() {
		ElementTO et=new ElementTO(this.getPlayground(), this.getId(),this.getLocation(), this.getName(),this.getCreationDate(),
				this.getExpirationDate(),this.getType(),this.getAttributes(),
				this.getCreatorPlayground(), this.getCreatorEmail());
		return et;
		
	}

	

}
