package playground.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
	
	private final String MANAGEREMAIL="nadav@ac.il",PLAYEREMAIL="nick@ac.il",PLAYGROUNDNAME="TA";
	private ElementEntity MessageBoard,Tamagotchi;
	private UserEntity manager,player;
	
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
		
		manager=this.userService.addNewUser(new UserEntity(new UserKey(MANAGEREMAIL, PLAYGROUNDNAME), "Nadi", "---Picture---", "Manager", 0L));
		player=this.userService.addNewUser(new UserEntity(new UserKey(PLAYEREMAIL, PLAYGROUNDNAME), "Nick", "---Picture---", "Player", 0L));
		this.userService.confirm(PLAYGROUNDNAME, MANAGEREMAIL, manager.getCode());
		this.userService.confirm(PLAYGROUNDNAME, PLAYEREMAIL, player.getCode());
		
		
		MessageBoard=new ElementEntity();
		MessageBoard.setType("message_board");
		MessageBoard.setName("Test Message");
		MessageBoard.setCreatorEmail(MANAGEREMAIL);
		MessageBoard.setCreatorPlayground(PLAYGROUNDNAME);
		MessageBoard.setCreationDate(new Date());
		MessageBoard.setExpirationDate(null);
		
		MessageBoard=this.elementService.addNewElement(PLAYGROUNDNAME, MANAGEREMAIL, MessageBoard);
		
		
		Tamagotchi=new ElementEntity();
		Tamagotchi.setType("Tamagotchi");
		Tamagotchi.setName("Test Tamagotchi");
		Tamagotchi.setCreatorEmail(MANAGEREMAIL);
		Tamagotchi.setCreatorPlayground(PLAYGROUNDNAME);
		Tamagotchi.setCreationDate(new Date());
		Tamagotchi.setExpirationDate(null);
		
		Tamagotchi=this.elementService.addNewElement(PLAYGROUNDNAME, MANAGEREMAIL, Tamagotchi);
	

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
	public void testPlayerPostsMessageSuccessfully() throws Exception {

		String postJson = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World\"}}";

		ActivityTO post = this.jacksonMaper.readValue(postJson, ActivityTO.class);
		post.setElementId(MessageBoard.getPlaygroundAndID().getId());
		long oldPoint=player.getPoints();
		ActivityTO response = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", post,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);
		



		assertThat(response).isNotNull().extracting("playerPlayground", "type").containsExactly(PLAYGROUNDNAME, post.getType());

		

		this.player = this.userService.getUserByEmailAndPlayground(new UserKey(PLAYEREMAIL,PLAYGROUNDNAME));
		
		assertThat(this.player.getPoints()>oldPoint).isTrue();	
		
		
		
		ElementEntity updatedMessageBoardByActivityResponse = this.elementService.getElementById(response.getPlayerPlayground(), response.getPlayerEmail(), response.getElementPlayground(), response.getElementId());
		
		String old=(String)updatedMessageBoardByActivityResponse.getAttributes().get("msgColor");
		
		Map expectedMapElement = this.jacksonMaper.readValue(
				"{\"msgColor\": \"" + old + "\"}",
				Map.class);
		
		Map expectedMapActivity = this.jacksonMaper.readValue(
				"{\"message\": \"Hello World\"}",
				Map.class);
		
		this.MessageBoard = this.elementService.getElementById(PLAYGROUNDNAME, MANAGEREMAIL, PLAYGROUNDNAME, this.MessageBoard.getPlaygroundAndID().getId());
		
		assertThat(response.getAttributes().get("message")).isEqualTo(expectedMapActivity.get("message"));
		assertThat(this.MessageBoard.getAttributes()).isEqualTo(expectedMapElement);

	}
	@Test(expected = Exception.class)
	public void testManagerPostMessageFailed() throws Exception {

		String postJson = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World\"}}";

		ActivityTO post = this.jacksonMaper.readValue(postJson, ActivityTO.class);
		post.setElementId(MessageBoard.getPlaygroundAndID().getId());
		this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", post,
				ActivityTO.class, PLAYGROUNDNAME, MANAGEREMAIL);
	}
	
	@Test(expected = Exception.class)
	public void testPlayerPostMessageFailedElementNotExist() throws Exception {


		String postJson = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World\"}}";

		ActivityTO post = this.jacksonMaper.readValue(postJson, ActivityTO.class);
		post.setElementId("NOTEXIST");
		this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", post,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);

	}
	@Test(expected = Exception.class)
	public void testPlayerPostsMessageFailedWrongType() throws Exception {

		String postJson = "{\"type\":\"PostMessageNotExist\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World\"}}";
		ActivityTO post = this.jacksonMaper.readValue(postJson, ActivityTO.class);
		post.setElementId(MessageBoard.getPlaygroundAndID().getId());
		this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", post,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);


	}
	@Test
	public void testShowMessagesWithPagination() throws Exception {
		String postJson1 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World1\"}}";
		String postJson2 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World2\"}}";
		String postJson3 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World3\"}}";
		
		ActivityTO postMsg1 = this.jacksonMaper.readValue(postJson1, ActivityTO.class);
		ActivityTO postMsg2 = this.jacksonMaper.readValue(postJson2, ActivityTO.class);
		ActivityTO postMsg3 = this.jacksonMaper.readValue(postJson3, ActivityTO.class);
		

		postMsg1.setElementId(this.MessageBoard.getPlaygroundAndID().getId());
		postMsg2.setElementId(this.MessageBoard.getPlaygroundAndID().getId());
		postMsg3.setElementId(this.MessageBoard.getPlaygroundAndID().getId());

		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg1);
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg2);
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg3);

		String forPostJson = "{\"type\":\"GetAllMessages\",\"elementPlayground\":\"TA\",\"elementId\": \"xx\",\"attributes\":{}}";

		ActivityTO forPost = this.jacksonMaper.readValue(forPostJson, ActivityTO.class);
		forPost.setElementId(this.MessageBoard.getPlaygroundAndID().getId());

		ActivityTO response = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", forPost,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);


		assertThat((List<Object>)(response.getAttributes().get("messages"))).hasSize(3);
	}
	@Test
	public void testShowMessagesWithPaginationLimit2perPagebyPlayer() throws Exception {
		String postJson1 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World1\"}}";
		String postJson2 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World2\"}}";
		String postJson3 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World3\"}}";
		
		ActivityTO postMsg1 = this.jacksonMaper.readValue(postJson1, ActivityTO.class);
		ActivityTO postMsg2 = this.jacksonMaper.readValue(postJson2, ActivityTO.class);
		ActivityTO postMsg3 = this.jacksonMaper.readValue(postJson3, ActivityTO.class);
		

		postMsg1.setElementId(this.MessageBoard.getPlaygroundAndID().getId());
		postMsg2.setElementId(this.MessageBoard.getPlaygroundAndID().getId());
		postMsg3.setElementId(this.MessageBoard.getPlaygroundAndID().getId());

		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg1);
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg2);
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg3);

		String forPostJson = "{\"type\":\"GetAllMessages\",\"elementPlayground\":\"TA\",\"elementId\": \"xx\",\"attributes\":{\"page\":0,\"size\":2}}";

		ActivityTO forPost = this.jacksonMaper.readValue(forPostJson, ActivityTO.class);
		forPost.setElementId(this.MessageBoard.getPlaygroundAndID().getId());

		ActivityTO response = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", forPost,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);


		assertThat((List<Object>)(response.getAttributes().get("messages"))).hasSize(2);
	}
	@Test(expected = Exception.class)
	public void testShowActivitiesUsingInvalidPageNumber() throws Exception {
		String postJson1 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World1\"}}";
		String postJson2 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World2\"}}";
		String postJson3 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World3\"}}";
		
		ActivityTO postMsg1 = this.jacksonMaper.readValue(postJson1, ActivityTO.class);
		ActivityTO postMsg2 = this.jacksonMaper.readValue(postJson2, ActivityTO.class);
		ActivityTO postMsg3 = this.jacksonMaper.readValue(postJson3, ActivityTO.class);
		

		postMsg1.setElementId(this.MessageBoard.getPlaygroundAndID().getId());
		postMsg2.setElementId(this.MessageBoard.getPlaygroundAndID().getId());
		postMsg3.setElementId(this.MessageBoard.getPlaygroundAndID().getId());

		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg1);
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg2);
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg3);

		String forPostJson = "{\"type\":\"GetAllMessages\",\"elementPlayground\":\"TA\",\"elementId\": \"xx\",\"attributes\":{\"page\":-1,\"size\":2}}}";

		ActivityTO forPost = this.jacksonMaper.readValue(forPostJson, ActivityTO.class);
		forPost.setElementId(this.MessageBoard.getPlaygroundAndID().getId());

		ActivityTO response = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", forPost,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);
	}
	@Test
	public void testShowMessagesWithPaginationByElementId() throws Exception {
		String postJson1 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World1\"}}";
		String postJson2 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World2\"}}";
		String postJson3 = "{\"type\":\"PostMessage\",\"elementPlayground\":\"TA\",\"attributes\":{\"message\":\"Hello World3\"}}";
		
		ActivityTO postMsg1 = this.jacksonMaper.readValue(postJson1, ActivityTO.class);
		ActivityTO postMsg2 = this.jacksonMaper.readValue(postJson2, ActivityTO.class);
		ActivityTO postMsg3 = this.jacksonMaper.readValue(postJson3, ActivityTO.class);
		
		ElementEntity MessageBoard2=new ElementEntity();
		MessageBoard2.setType("message_board");
		MessageBoard2.setCreatorEmail(MANAGEREMAIL);
		MessageBoard2.setCreatorPlayground(PLAYGROUNDNAME);
		MessageBoard2.setCreationDate(new Date());
		MessageBoard2.setExpirationDate(null);
		
		MessageBoard2=this.elementService.addNewElement(PLAYGROUNDNAME, MANAGEREMAIL, MessageBoard2);

		postMsg1.setElementId(this.MessageBoard.getPlaygroundAndID().getId());
		postMsg2.setElementId(MessageBoard2.getPlaygroundAndID().getId());
		postMsg3.setElementId(this.MessageBoard.getPlaygroundAndID().getId());
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg1);
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg2);
		this.actService.createActivity(PLAYGROUNDNAME, PLAYEREMAIL, postMsg3);

		String forPostJson = "{\"type\":\"GetAllMessagesByElementId\",\"elementPlayground\":\"TA\",\"elementId\": \"xx\",\"attributes\":{}}";

		ActivityTO forPost = this.jacksonMaper.readValue(forPostJson, ActivityTO.class);
		forPost.getAttributes().put("elementId", this.MessageBoard.getPlaygroundAndID().getId());
		forPost.setElementId(this.MessageBoard.getPlaygroundAndID().getId());

		ActivityTO response = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", forPost,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);


		assertThat((List<Object>)(response.getAttributes().get("messages"))).hasSize(2);
	}
	
	@Test
	public void testTamagotchiFeedSuccessfulyByPlayer() throws Exception {
		String postJson ="{\"playground\": \"TA\", \"elementPlayground\": \"TA\", \"type\": \"TamagotchiFeed\",\"playerEmail\":\"nick@ac.il\",\"playerPlayground\": \"TA\",\"attributes\": {\"isAlive\" : true, \"points\" : 0, \"happiness\": 0, \"fed\": 0,\"life\":0}}";

		ActivityTO post = this.jacksonMaper.readValue(postJson, ActivityTO.class);
		post.setElementId(Tamagotchi.getPlaygroundAndID().getId());
		
		
		ActivityTO response = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", post,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);
		

		assertThat(response).isNotNull().extracting("playerPlayground", "type").containsExactly(PLAYGROUNDNAME, post.getType());

		this.player = this.userService.getUserByEmailAndPlayground(new UserKey(PLAYEREMAIL,PLAYGROUNDNAME));
		
		assertThat(this.player.getPoints() == 5).isTrue();	
		
		
		ActivityTO expectedAct = this.jacksonMaper.readValue(
				"{\"playground\": \"TA\", \"elementPlayground\": \"TA\", \"type\": \"TamagotchiFeed\",\"playerEmail\":\"nick@ac.il\",\"playerPlayground\": \"TA\",\"attributes\": {\"isAlive\" : true, \"points\" : 5, \"happiness\": 90, \"fed\": 60,\"life\":50}}",
				ActivityTO.class);
		expectedAct.setId(null);
		expectedAct.setElementId(Tamagotchi.getPlaygroundAndID().getId());
		String expectedJsonResponse = this.jacksonMaper.writeValueAsString(expectedAct);
		response.setId(null);
		
		
		assertThat(this.jacksonMaper.writeValueAsString(response)).isEqualTo(expectedJsonResponse);
	}
	@Test
	public void testTamagotchiWalkSuccessfulyByPlayer() throws Exception {
		String postJson = "{\"type\":\"TamagotchiWalk\",\"elementPlayground\":\"TA\",\"attributes\": {\"isAlive\" : true, \"points\" : 5, \"happiness\": 90,\"life\":70,  \"fed\": 40}}}";

		ActivityTO post = this.jacksonMaper.readValue(postJson, ActivityTO.class);
		post.setElementId(Tamagotchi.getPlaygroundAndID().getId());
		
		ActivityTO response = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", post,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);
		

		assertThat(response).isNotNull().extracting("playerPlayground", "type").containsExactly(PLAYGROUNDNAME, post.getType());

		this.player = this.userService.getUserByEmailAndPlayground(new UserKey(PLAYEREMAIL,PLAYGROUNDNAME));
		
		assertThat(this.player.getPoints() == 5).isTrue();	
		
		
		ActivityTO expectedAct = this.jacksonMaper.readValue(
				"{\"playground\": \"TA\", \"elementPlayground\": \"TA\", \"type\": \"TamagotchiWalk\",\"playerEmail\":\"nick@ac.il\",\"playerPlayground\": \"TA\",\"attributes\": {\"isAlive\" : true, \"points\" : 5, \"happiness\": 90,\"life\":70,\"fed\": 40}}",
				ActivityTO.class);
		expectedAct.setId(null);
		expectedAct.setElementId(Tamagotchi.getPlaygroundAndID().getId());
		String expectedJsonResponse = this.jacksonMaper.writeValueAsString(expectedAct);
		response.setId(null);

		
		assertThat(this.jacksonMaper.writeValueAsString(response)).isEqualTo(expectedJsonResponse);
	
	}
	@Test
	public void testTamagotchiTickleSuccessfulyByPlayer() throws Exception {
		String postJson ="{\"playground\": \"TA\", \"elementPlayground\": \"TA\", \"type\": \"TamagotchiTickle\",\"playerEmail\":\"nick@ac.il\",\"playerPlayground\": \"TA\",\"attributes\": {\"isAlive\" : true, \"points\" : 0, \"happiness\": 0, \"fed\": 0,\"life\":0}}";

		ActivityTO post = this.jacksonMaper.readValue(postJson, ActivityTO.class);
		post.setElementId(Tamagotchi.getPlaygroundAndID().getId());
		
		
		ActivityTO response = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", post,
				ActivityTO.class, PLAYGROUNDNAME, PLAYEREMAIL);
		

		assertThat(response).isNotNull().extracting("playerPlayground", "type").containsExactly(PLAYGROUNDNAME, post.getType());

		this.player = this.userService.getUserByEmailAndPlayground(new UserKey(PLAYEREMAIL,PLAYGROUNDNAME));
		
		assertThat(this.player.getPoints() == 5).isTrue();	
		
		
		ActivityTO expectedAct = this.jacksonMaper.readValue(
				"{\"playground\": \"TA\", \"elementPlayground\": \"TA\", \"type\": \"TamagotchiTickle\",\"playerEmail\":\"nick@ac.il\",\"playerPlayground\": \"TA\",\"attributes\": {\"isAlive\" : true, \"points\" : 5, \"happiness\": 100, \"fed\": 40,\"life\":50}}",
				ActivityTO.class);
		expectedAct.setId(null);
		expectedAct.setElementId(Tamagotchi.getPlaygroundAndID().getId());
		String expectedJsonResponse = this.jacksonMaper.writeValueAsString(expectedAct);
		response.setId(null);
		
		
		assertThat(this.jacksonMaper.writeValueAsString(response)).isEqualTo(expectedJsonResponse);
	
	}


}
