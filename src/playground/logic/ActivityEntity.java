package playground.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EmbeddedId;
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

	private ActivityKey playgroundAndId;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerEmail;
	private String playerPlayground;
	private Map<String, Object> attributes;
	

	public ActivityEntity() {
	this.attributes = new HashMap<>();
	}

	public ActivityEntity(ActivityKey actKey,String elementPlayground, String elementId, String type, String playerEmail,
			String playerPlayground, Map<String, Object> attributes) {
		this();
		this.playgroundAndId = actKey;
		this.elementPlayground = elementPlayground;
		this.elementId = elementId;
		this.type = type;
		this.playerEmail = playerEmail;
		this.playerPlayground = playerPlayground;
		this.attributes = attributes;
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
	@EmbeddedId
	public ActivityKey getPlaygroundAndId() {
		return playgroundAndId;
	}


	public void setPlaygroundAndId(ActivityKey playgroundAndId) {
		this.playgroundAndId = playgroundAndId;
	}
	@Transient
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public String MessageFromAttributes() {
		return (String)this.getAttributes().get("message");
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
		return "ActivityEntity [playground = " + this.playgroundAndId.getPlayground() + ", id = " + this.playgroundAndId.getId() + ", elementPlayground = " + this.elementPlayground
				+ ", elementId = " + this.elementId + ", type = " + this.type + ", playerEmail = " + this.playerEmail
				+ ", playerPlayground = " + this.playerPlayground + ", attributes = " + this.attributes + "]";
	}
	
}
