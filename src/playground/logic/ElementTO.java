package playground.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ElementTO {

	private String playground;
	private String id;
	private Location location;
	private String name;
	private Date creationDate;
	private Date expirationDate;
	private String type;
	private Map<String, Object> attributes= new HashMap<>();
	private String creatorPlayground;
	private String creatorEmail;

	public ElementTO() {
		this.creationDate = new Date();
		this.attributes = new HashMap<>();
		this.location = new Location(0, 0);
	}

	public ElementTO(ElementEntity element) {
		if (element.getX() != null && element.getY() != null) {
			this.playground = element.getPlaygroundAndID().getPlayground();
			this.id = element.getPlaygroundAndID().getId();
			this.location = new Location(element.getX(), element.getY());
			this.name = element.getName();
			this.creationDate = element.getCreationDate();
			this.expirationDate = element.getExpirationDate();
			this.type = element.getType();
			this.attributes = element.getAttributes();
			this.creatorPlayground = element.getCreatorPlayground();
			this.creatorEmail = element.getCreatorEmail();
		} else {
			this.playground = element.getPlaygroundAndID().getPlayground();
			this.id = element.getPlaygroundAndID().getId();
			this.location = null;
			this.name = element.getName();
			this.creationDate = element.getCreationDate();
			this.expirationDate = element.getExpirationDate();
			this.type = element.getType();
			this.attributes = element.getAttributes();
			this.creatorPlayground = element.getCreatorPlayground();
			this.creatorEmail = element.getCreatorEmail();
		}
	}
	
	

	public ElementTO(String playground, String id, String name, String creatorPlayground, String creatorEmail) {
		this.playground = playground;
		this.id = id;
		this.location = new Location(0, 0);
		this.name = name;
		this.creationDate = new Date();
		this.expirationDate = null;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
		this.attributes = new HashMap<>();
	}

	public ElementTO(String playground, String id, Location location, String name, Date creationDate,
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

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;

	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;

	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;

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

	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;

	}

	public ElementEntity toEntity() {
		if (this.location != null)
			return new ElementEntity(new ElementKey(this.id, this.playground), this.location.getX(), this.location.getY(), this.name,
					this.creationDate, this.expirationDate, this.type, this.attributes, this.creatorPlayground,
					this.creatorEmail);

		return new ElementEntity(new ElementKey(this.id, this.playground), null, null, this.name, this.creationDate,
				this.expirationDate, this.type, this.attributes, this.creatorPlayground, this.creatorEmail);

	}

	@Override
	public String toString() {
		return "ElementTO [playground=" + playground + ", id=" + id + ", location=" + location + ", name=" + name
				+ ", creationDate=" + creationDate + ", expirationDate=" + expirationDate + ", type=" + type
				+ ", attributes=" + attributes + ", creatorPlayground=" + creatorPlayground + ", creatorEmail="
				+ creatorEmail + "]";
	}
	
	

}
