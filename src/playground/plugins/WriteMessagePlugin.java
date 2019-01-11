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
public class WriteMessagePlugin implements PlaygroundPlugin {

	private ObjectMapper jackson;
	private ElementService elements;
	private UserService user;

	@Autowired
	public  WriteMessagePlugin(UserService user,ElementService elements) {

		this.jackson = new ObjectMapper();
		this.user = user;
		this.elements = elements;

	}

	@Override
	public Object invokeOperation( ActivityEntity activity) throws Exception {
		String id = activity.getElementId();
		String elementPlaygrund = activity.getElementPlayground();
		String plPlayground = activity.getPlayerPlayground();
		String plEmail = activity.getPlayerEmail();
		UserKey userKey = new UserKey(plEmail,plPlayground);
		
		Message value = this.jackson.readValue(activity.getJsonAttributes(), Message.class);
		
		UserEntity currentUser = user.getUserByEmailAndPlayground(userKey);
		currentUser.setPoints(currentUser.getPoints() + 2);
		user.updateUser(currentUser, userKey);
		
		return new PostMessageResponse(value.getMessage(),2);

	}

}
