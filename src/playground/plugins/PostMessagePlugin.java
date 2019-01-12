package playground.plugins;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.ActivityEntity;
import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.exceptions.ElementAlreadyExistsException;
import playground.logic.exceptions.NoSuchElementID;

@Component
public class PostMessagePlugin implements PlaygroundPlugin {

	private ObjectMapper jackson;
	private ElementService elements;
	private UserService users;
	private String points;
	

	@Autowired
	public  PostMessagePlugin(UserService users,ElementService elements) {

		this.jackson = new ObjectMapper();
		this.users = users;
		this.elements = elements;

	}

	@Override
	public Object invokeOperation(ActivityEntity activity, ElementEntity element, UserEntity user) throws Exception {


		String plPlayground = activity.getPlayerPlayground();
		String plEmail = activity.getPlayerEmail();
		UserKey userKey = user.getUserEmailPlaygroundKey();
		
		Message value = this.jackson.readValue(activity.getJsonAttributes(), Message.class);
		
		
		ElementMsgColor elementMsgCol = new ElementMsgColor();
		element.setAttributes(jackson.readValue(jackson.writeValueAsString(elementMsgCol), Map.class));
		
		UserEntity currentUser = this.users.getUserByEmailAndPlayground(userKey);
		PostMessageResponse rv;
		if(elementMsgCol.getMsgColor().equals("Red")) {
			currentUser.setPoints(currentUser.getPoints() + 5);
			rv=new PostMessageResponse(value.getMessage(),5);
		}
		else {
			currentUser.setPoints(currentUser.getPoints() + 2);
			rv=new PostMessageResponse(value.getMessage(),2);
			}
		
		this.users.updateUser(currentUser, userKey);
		this.elements.updateElementById(plPlayground, plEmail, element.getPlaygroundAndID().getPlayground(), element.getPlaygroundAndID().getId(), element, true);
		return rv;

	}



}
