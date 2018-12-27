package playground.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;


import playground.Application;
import playground.logic.ActivityEntity;
import playground.logic.ActivityService;
import playground.logic.ActivityTO;
import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.UserTO;
import playground.logic.exceptions.ElementAlreadyExistsException;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes = Application.class)
public class ActivityTests {

	@Autowired
	private  ActivityService actService;
	@Autowired
	private UserService userService;
	@Autowired
	private ElementService elementService;
	

	@LocalServerPort
	private int port;
	private RestTemplate restTemplate;
	private String url;
	private ObjectMapper jacksonMaper;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/activity";

		//// Jackson init
		this.jacksonMaper = new ObjectMapper();
	}

	@Before
	public void setup () throws Exception {
//		String creatorPlayground = "TA";
//		String creatorEmail = "benny@ac.il";
//		UserKey userKey = new UserKey(creatorEmail, creatorPlayground);
//		//TODO: persisting data --> this lines not needed
//		//create new user and validate his account
//		UserEntity currentUser = userService.addNewUser(new UserEntity(userKey, "Nadi", "Any", "Manager", 0L));
//		currentUser.setIsValidate(true);
//		userService.updateUser(currentUser, userKey);
	

		

	}
	@After
	public void teardown() {
		this.elementService.cleanup();
		this.userService.cleanup();
		this.actService.cleanup();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {

		
	}
	
	@Test
	public void testReadMessagesSuccessfully()  throws Exception {
		String activityId = "123";
		
		ElementEntity messageBoard = new ElementEntity();
		messageBoard.setType("Message-Borad"); 
		messageBoard.setName("messageBoard");
		messageBoard.setId("123");
		String creatorPlayground = "TA";
		String creatorEmail = "benny@ac.il";
		
		UserKey userKey = new UserKey(creatorEmail, creatorPlayground);
		UserEntity currentUser = userService.addNewUser(new UserEntity(userKey, "Nadi", "Any", "Manager", 0L));
		currentUser.setIsValidate(true);
		userService.updateUser(currentUser, userKey);
		
		ActivityEntity postActivity = new ActivityEntity( "TA", activityId, "readMessages",creatorEmail,"Nadav",null);
		elementService.addNewElement(creatorPlayground, creatorEmail, messageBoard);
		
		ActivityTO rv = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}" ,postActivity,ActivityTO.class, "TA", creatorEmail);
	}

	@Test
	public void testWriteMessageSuccessfully() throws Exception {
		String activityId = "123";
		
		ElementEntity messageBoard = new ElementEntity();
		messageBoard.setType("Message-Borad"); 
		messageBoard.setName("messageBoard");
		messageBoard.setId("123");
		String creatorPlayground = "TA";
		String creatorEmail = "benny@ac.il";
		Map<String,Object> attributes = new HashMap<String, Object>() ;
		attributes.put("message", "hi");
		
		//TODO: change to sets
		ActivityEntity postActivity = new ActivityEntity( "TA", activityId, "WriteMessage",creatorEmail,"Nadav",attributes);
		
		UserKey userKey = new UserKey(creatorEmail, creatorPlayground);
		UserEntity currentUser = userService.addNewUser(new UserEntity(userKey, "Nadi", "Any", "Manager", 0L));
		currentUser.setIsValidate(true);
		userService.updateUser(currentUser, userKey);
		
		elementService.addNewElement(creatorPlayground, creatorEmail, messageBoard);
		
		ActivityTO rv = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}" ,postActivity,ActivityTO.class, "TA", creatorEmail);
		
		String mapAttributesToCheck = jacksonMaper.writeValueAsString(rv.getAttributes());
		
		ActivityEntity returnedActivity = this.actService.getActivity(rv.getId());
		String mapAttributesOfReturnedEntity = jacksonMaper.writeValueAsString(returnedActivity.getAttributes());
		
		
		assertThat(returnedActivity).
		isNotNull();
		
		assertThat(mapAttributesToCheck).isEqualTo(mapAttributesOfReturnedEntity);
		assertThat(userService.getUserByEmailAndPlayground(userKey)).extracting("points").containsExactly(2L);
		
		
	
		
		
		
		
		
	}




	@Test
	public void theUserInvokeActivitySuccessfully() throws Exception {
		/*************************************************************
		 *	Given the server is up 									 *
		 *	When I POST playground/activity/TA/spivak@ac.il			 *
		 *	{"id": "123","playground":"TA","type": "pMessage"} 		 *
		 *************************************************************/

		String actJson = "{\"id\": \"123\",\"playground\":\"TA\",\"type\": \"pMessage\"}";
		ActivityEntity act = this.jacksonMaper.readValue(actJson, ActivityEntity.class);

		ActivityTO forPost = new ActivityTO(act);

		ActivityTO response = this.restTemplate.postForObject(
				this.url+"/{userPlayground}/{email}",
				forPost,
				ActivityTO.class,
				"TA","spivak@ac.il");
		/********************************************************
		 *	Then the return status is 200					 	*
		 *	And the returned value is the posted activity 	 	*
		 *	{"id": "123","playground":"TA","type": "pMessage"} 	*
		 ********************************************************/
		assertThat(response)
		.isNotNull()
		.extracting("id","playground","type")
		.contains("123","TA","pMessage");
	}

	@Test(expected=Exception.class)
	public void theUserInvokeActivityWithWrongType() throws Exception {
		/*************************************************************
		 *	Given the server is up 									 *
		 *	When I POST playground/activity/TA/spivak@ac.il			 *
		 *	{"id": "123","playground":"TA","type": "post"}			 *
		 *************************************************************/

		String actJson = "{\"id\": \"123\",\"playground\":\"TA\",\"type\": \"none\"}";
		ActivityEntity act = this.jacksonMaper.readValue(actJson, ActivityEntity.class);

		ActivityTO forPost = new ActivityTO(act);

		ActivityTO response = this.restTemplate.postForObject(
				this.url+"/{userPlayground}/{email}",
				forPost,
				ActivityTO.class,
				"TA","spivak@ac.il");
		/****************************************
		 * Then the return status is <> 2xx     *
		 ****************************************/

	}
}
