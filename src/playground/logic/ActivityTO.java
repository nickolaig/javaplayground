package playground.logic;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ActivityTO {

	private String playground;
	private String id;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private Map<String, Object> attributes;

	public ActivityTO() {
		this.attributes = new HashMap<>();
	}

	// basic constructor
	public ActivityTO(String playground, String id, String elementPlayground, String elementId, String type,
			String playerPlayground, String playerEmail, Map<String, Object> attributes) {
		super();
		this.playground = playground;
		this.id = id;
		this.elementPlayground = elementPlayground;
		this.elementId = elementId;
		this.type = type;
		this.playerPlayground = playerPlayground;
		this.playerEmail = playerEmail;
		this.attributes = attributes;
	}
	
	// constructor for testing
	public ActivityTO(ActivityEntity act) {
		this();
		
		if(act!=null) {
		this.playground = "TA";
		this.id = act.getId();
		this.elementPlayground = act.getElementPlayground();
		this.elementId = act.getElementId();
		this.type = act.getType();
		this.playerEmail = act.getPlayerEmail();
		this.playerPlayground = act.getPlayerPlayground();
		this.attributes = act.getAttributes();
		}
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

	public String getPlayerPlayground() {
		return playerPlayground;
	}

	public void setPlayerPlayground(String playerPlayground) {
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String toString() {
		return "ActivityTO [playground = " +this.playground + ", id = " +this.id + ", elementPlayground = " + this.elementPlayground
				+ ", elementId = " + this.elementId + ", type = " + this.type + ", playerEmail = " + this.playerEmail 
				+ ", playerPlayground = " + this.playerPlayground + ", attributes = " + this.attributes + "]";
	}
	
	public ActivityEntity toEntity() {
		
		ActivityEntity act = new ActivityEntity();
		
		act.setAttributes(this.attributes);
		act.setElementId(this.elementId);
		act.setElementPlayground(this.elementPlayground);
		act.setId(this.id);
		act.setPlayerEmail(this.playerEmail);
		act.setPlayerPlayground(this.playerPlayground);
		act.setPlayground(this.playground);
		act.setType(this.type);
		
		return act;
		
	}

}
