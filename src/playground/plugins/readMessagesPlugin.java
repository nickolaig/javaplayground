package playground.plugins;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.ActivityEntity;
import playground.logic.ActivityService;
import playground.logic.ElementService;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;

@Component
public class readMessagesPlugin implements PlaygroundPlugin{
	private ObjectMapper jackson;
	private ElementService elements;
	private UserService user;
	private ActivityService activities;
	
	@Autowired
	public  readMessagesPlugin(UserService user,ElementService elements, ActivityService activities) {

		this.jackson = new ObjectMapper();
		this.user = user;
		this.elements = elements;
		this.activities = activities;
	}

	@Override
	public Object invokeOperation( ActivityEntity activity) throws Exception {
		String id = activity.getElementId();
		String elementPlaygrund = activity.getElementPlayground();
		String plPlayground = activity.getPlayerPlayground();
		String plEmail = activity.getPlayerEmail();
		UserKey userKey = new UserKey(plEmail,plPlayground);
		
		try {
			MessagePageable pageable = this.jackson.readValue(activity.getJsonAttributes(), MessagePageable.class);
			
			return new AllMessages(
					this.activities.getAllPostMessagesByElementId(activity.getElementId(), activity.getType(), 
						PageRequest.of(0, 5))
					.stream()
					.map(ActivityEntity::MessageFromAttributes)
					.collect(Collectors.toList())
					);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	
//		
//		Message value = this.jackson.readValue(activity.getJsonAttributes(), Message.class);
//		UserEntity currentUser = user.getUserByEmailAndPlayground(userKey);
//		currentUser.setPoints(currentUser.getPoints() + 2);
//		user.updateUser(currentUser, userKey);
//		
//		return new PostMessageResponse(value.getMessage(),2);

	}

}
