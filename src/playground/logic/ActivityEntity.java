package playground.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "activity")
public class ActivityEntity {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	private String id;
	private String playground;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerEmail;
	private String playerPlayground;
	private Map<String, Object> attributes;
	
	public AtomicLong atomiclong = new AtomicLong(1L);
	
	private String defaultPlaygroundName;
	
	
	@Value("${playground.name:Anonymous}")
	public void setDefaultPlaygroundName(String defaultPlaygroundName) {
		this.defaultPlaygroundName = defaultPlaygroundName;
	}
	
	
	public ActivityEntity() {
	this.attributes = new HashMap<>();
	}

	public ActivityEntity(String elementPlayground, String elementId, String type, String playerEmail,
			String playerPlayground, Map<String, Object> attributes) {
		this();
		this.id = Long.toString(atomiclong.getAndIncrement());
		this.playground=this.defaultPlaygroundName;
		this.elementPlayground = elementPlayground;
		this.elementId = elementId;
		this.type = type;
		this.playerEmail = playerEmail;
		this.playerPlayground = playerPlayground;
		this.attributes = attributes;
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

	public String getElementPlayground() {
		return elementPlayground;
	}

	public void setElementPlayground(String elementPlayground) {
		this.elementPlayground = elementPlayground;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public String getPlayerPlayground() {
		return playerPlayground;
	}

	public void setPlayerPlayground(String playerPlayground) {
		this.playerPlayground = playerPlayground;
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
	
	@SuppressWarnings("unchecked")
	public void setJsonAttributes(String jsonAttributes) {
		try {
			this.attributes = new ObjectMapper().readValue(jsonAttributes, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "ActivityEntity [playground = " + this.playground + ", id = " + this.id + ", elementPlayground = " + this.elementPlayground
				+ ", elementId = " + this.elementId + ", type = " + this.type + ", playerEmail = " + this.playerEmail
				+ ", playerPlayground = " + this.playerPlayground + ", attributes = " + this.attributes + "]";
	}
	
}
