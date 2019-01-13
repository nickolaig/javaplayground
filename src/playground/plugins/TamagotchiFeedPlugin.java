package playground.plugins;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import playground.aop.checkForElementDisabled;
import playground.logic.ActivityEntity;
import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.exceptions.ElementAlreadyExistsException;
import playground.logic.exceptions.NoSuchElementID;

@Component
public class TamagotchiFeedPlugin implements PlaygroundPlugin {

	private ObjectMapper jackson;
	private ElementService elements;
	private UserService users;
	private String points;
	

	@Autowired
	public  TamagotchiFeedPlugin(UserService users,ElementService elements) {

		this.jackson = new ObjectMapper();
		this.users = users;
		this.elements = elements;

	}
	@checkForElementDisabled
	@Override
	public Object invokeOperation( ElementEntity element, UserEntity user, ActivityEntity activity) throws Exception {

		Integer points=0;
		String plPlayground = activity.getPlayerPlayground();
		String plEmail = activity.getPlayerEmail();
		UserKey userKey = user.getUserEmailPlaygroundKey();
		System.err.println("dsadhjasjkdahsjkdhaskjdhjksahdjksah"+element.getJsonAttributes()+"dsadhjasjkdahsjkdhaskjdhjksahdjksah");
		Tamagotchi value = this.jackson.readValue(element.getJsonAttributes(), Tamagotchi.class);

		if(value.getIsAlive()) {
			value.increaseHappiness(40);
			value.increaseFed(10);

			points+=5;
		}			
		if(!value.getIsAlive())
		{
			System.err.println("Your Tamagotchi died!:(");
			element.setType("TamagotchiDisabled");
			points-=10;
		}
		System.err.println("dsadhjasjkdahsjkdhaskjdhjksahdjksah"+element.getJsonAttributes()+"dsadhjasjkdahsjkdhaskjdhjksahdjksah");
		element.getAttributes().putAll(this.jackson.readValue(jackson.writeValueAsString(value), Map.class));
		System.err.println("dsadhjasjkdahsjkdhaskjdhjksahdjksah"+element.getJsonAttributes()+"dsadhjasjkdahsjkdhaskjdhjksahdjksah");
		UserEntity currentUser = this.users.getUserByEmailAndPlayground(userKey);
		TamagotchiResponse rv;
		
		currentUser.setPoints(currentUser.getPoints()+ points);
		rv=new TamagotchiResponse(value.getLife(),value.getHappiness(),value.getFed(),points,value.getIsAlive());
		
		this.users.updateUser(currentUser, userKey);
		this.elements.updateElementById(plPlayground, plEmail, element.getPlaygroundAndID().getPlayground(), element.getPlaygroundAndID().getId(), element, true);
		return rv;

	}



}
