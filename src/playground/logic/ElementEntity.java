package playground.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

//@Component
@Entity
@Table(name = "ELEMENTS")
public class ElementEntity {

	private String playground;
	private String id;
	// private Location location;
	private String name;
	private Date creationDate;
	private Date expirationDate;
	private String type;
	private Map<String, Object> attributes = new HashMap<>();
	private String creatorPlayground;
	private String creatorEmail;
	private Double x;
	private Double y;

	private String number;

	public ElementEntity() {

	}

	public ElementEntity(String id) {
		setPlayground("Test");
		this.attributes = new HashMap<>();
		this.id = id;

	}

	public ElementEntity(String playground, String id, Double x, Double y, String name, Date creationDate,
			Date expirationDate, String type, Map<String, Object> attributes, String creatorPlayground,
			String creatorEmail) {
		super();
		this.playground = playground;
		this.id = id;
		this.x = x;
		this.y = y;
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

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;

	}

	/*
	 * public Location getLocation() { return location; } public void
	 * setLocation(Location location) { this.location = location;
	 * this.attributes.put("location", this.location);
	 * 
	 * }
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	@Temporal(TemporalType.TIMESTAMP)
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

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	@Transient
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Lob
	public String getJsonAttributes() {
		try {
			return new ObjectMapper().writeValueAsString(this.attributes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setJsonAttributes(String jsonAttributes) {
		try {
			this.attributes = new ObjectMapper().readValue(jsonAttributes, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	public ElementTO toElementTO() {
		ElementTO et;
		if (this.x != null && this.y != null)
			et = new ElementTO(this.getPlayground(), this.getId(), new Location(this.x, this.y), this.getName(),
					this.getCreationDate(), this.getExpirationDate(), this.getType(), this.getAttributes(),
					this.getCreatorPlayground(), this.getCreatorEmail());
		else
			et = new ElementTO(this.getPlayground(), this.getId(), null, this.getName(), this.getCreationDate(),
					this.getExpirationDate(), this.getType(), this.getAttributes(), this.getCreatorPlayground(),
					this.getCreatorEmail());
		return et;

	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void addAttribute(String name, Object value) {
		this.attributes.put(name, value);

	}
}
