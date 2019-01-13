package playground.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
import playground.logic.ElementEntity;
import playground.logic.ElementKey;
import playground.logic.ElementService;
import playground.logic.ElementTO;
import playground.logic.Location;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.exceptions.ElementAlreadyExistsException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class ElementTests {
	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	private ObjectMapper jsonMapper;
	private final static String PLAYGROUND_NAME="TA";
	private final static String PLAYER_EMAIL = "niko@gmail.com";
	private final static String MANAGER_EMAIL = "kroyzman@gmail.com";
	private SimpleDateFormat formatter;
	
	@Autowired
	private ElementService elementService;
	@Autowired
	private UserService userService;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements";
		this.formatter = new SimpleDateFormat("dd-MM-yyyy");
		// Jackson init
		this.jsonMapper = new ObjectMapper();
	}

	@Before
	public void setup() throws Exception {
		
		UserEntity playerUser = this.userService.addNewUser(new UserEntity(new UserKey(PLAYER_EMAIL, PLAYGROUND_NAME), "nikos", "niko", "Player", 0L));
		UserEntity managerUser = this.userService.addNewUser(new UserEntity(new UserKey(MANAGER_EMAIL, PLAYGROUND_NAME), "nadav", "nadi", "Manager", 0L));	
		
		this.userService.confirm(PLAYGROUND_NAME, PLAYER_EMAIL, playerUser.getCode());
		this.userService.confirm(PLAYGROUND_NAME, MANAGER_EMAIL, managerUser.getCode());
		
	}

	@After
	public void teardown() {
		this.elementService.cleanup();
		this.userService.cleanup();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {

	}

	// Create new element
	@Test
	public void testManagerCreateElementSuccessfully() throws Exception {
		
		String entityJson = "{\"playground\": \"TA\", \"creatorEmail\": \"kroyzman@gmail.com\","
				+ "\"name\": \"Test\", \"type\": \"Tamagotchi\",\"location\":{\"x\":0 ,\"y\":0}}";
		
		ElementTO newElement = this.jsonMapper.readValue(entityJson, ElementTO.class);
		
		ElementTO rv = this.restTemplate.postForObject(this.url + "/{userPlayground}/{email}", newElement, ElementTO.class, PLAYGROUND_NAME, MANAGER_EMAIL);
		
		ElementEntity expectedEntityResult = rv.toEntity();
		
		ElementEntity elementFromDb = this.elementService.getElementById(PLAYGROUND_NAME, MANAGER_EMAIL, PLAYGROUND_NAME, rv.getId());
		
		assertThat(elementFromDb).isNotNull()
		.usingComparator((e1, e2) -> {
			int rvalue = e1.getPlaygroundAndID().getId().compareTo(e2.getPlaygroundAndID().getId());
			if(rvalue == 0) {
				return e1.getPlaygroundAndID().getPlayground().compareTo(e2.getPlaygroundAndID().getPlayground());
			}
			return rvalue;
		}).isEqualToIgnoringGivenFields(expectedEntityResult, "creationDate");
	}
	
	@Test(expected = Exception.class)
	public void testPlayerCreateElementFailed() throws Exception {

		String entityJson = "{\"playground\": \"TA\", \"creatorEmail\": \"niko@gmail.com\","
				+ "\"name\": \"Test\", \"type\": \"Tamagotchi\",\"location\":{\"x\":0 ,\"y\":0}}";

		ElementTO newElement = this.jsonMapper.readValue(entityJson, ElementTO.class);

		ElementTO rv = this.restTemplate.postForObject(this.url + "/{userPlayground }/{email}",
				newElement, ElementTO.class, PLAYGROUND_NAME, PLAYER_EMAIL);
	
//		Then the return status is <>2xx

	}
	
	
	// Update Element
	@Test
	public void testManagerUpdateElementSuccessfully() throws Exception {
		
		String entityJson = "{\"creatorPlayground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" ,"
				+ "\"x\":0,\"y\":0 ,\"name\":\"Test\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\"}";
		
		ElementEntity entity = this.jsonMapper.readValue(entityJson, ElementEntity.class);
		
		ElementEntity actualFullEntity = this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity);
		
		
		String entityJsonToUpdate = "{\"creatorPlayground\":\"TA\",\"playground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" "
				+ ",\"location\":{\"x\":1.0,\"y\":1.0},\"name\":\"ElementUpdateName\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\" ,\"attributes\": {\"Life\" : 50, \"Happiness\" : 50, \"Fed\" : 50}}";
		
		ElementTO toBeUpdated = this.jsonMapper.readValue(entityJsonToUpdate, ElementTO.class);
		toBeUpdated.setId(actualFullEntity.getPlaygroundAndID().getId());
		this.restTemplate.put(this.url + "/{userPlayground}/{email}/{playground}/{id} ", toBeUpdated, PLAYGROUND_NAME,
				MANAGER_EMAIL, PLAYGROUND_NAME, actualFullEntity.getPlaygroundAndID().getId());
		
		ElementEntity returnedEntityFromDb = this.elementService.getElementById(PLAYGROUND_NAME, MANAGER_EMAIL, PLAYGROUND_NAME, actualFullEntity.getPlaygroundAndID().getId());
		returnedEntityFromDb.setPlaygroundAndID(null);
		returnedEntityFromDb.setCreationDate(null);
		
		String expectedJson = this.jsonMapper.writeValueAsString(this.jsonMapper.readValue(
				"{\"playgroundAndID\":null,\"creatorPlayground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" "
						+ ",\"x\":1.0,\"y\":1.0,\"creationDate\":null,\"expirationDate\":null,\"type\":\"Tamagotchi\",\"name\":\"ElementUpdateName\""
						+ ",\"attributes\":{\"Life\" : 50, \"Happiness\" : 50, \"Fed\" : 50}}",
				ElementEntity.class));

		assertThat(this.jsonMapper.writeValueAsString(returnedEntityFromDb)).isEqualTo(expectedJson);

	}

	@Test(expected = Exception.class)
	public void testUpdateElementWithNonExistingElement() throws Exception {
		String id = "123";
		
		String entityJson = "{\"creatorPlayground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" ,"
				+ "\"x\":0,\"y\":0 ,\"name\":\"Test\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\"}";
		
		ElementEntity entity = this.jsonMapper.readValue(entityJson, ElementEntity.class);

		this.restTemplate.put(this.url + "/{userPlayground}/{email}/{playground}/{id} ", entity, PLAYGROUND_NAME,
				MANAGER_EMAIL, PLAYGROUND_NAME, id);
	}
	
	@Test(expected=Exception.class)
	public void testManagerFailToUpdateElementsPlayground() throws Exception {
		
		String entityJson = "{\"creatorPlayground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" ,"
				+ "\"x\":0,\"y\":0 ,\"name\":\"Test\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\"}";
		
		ElementEntity entity = this.jsonMapper.readValue(entityJson, ElementEntity.class);
		ElementEntity actualFullEntity = this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity);
		
		String JsonToUpdate = "{\"creatorPlayground\":\"TA\",\"playground\":\"Failed\",\"creatorEmail\":\"kroyzman@gmail.com\" "
				+ ",\"location\":{\"x\":1.0,\"y\":1.0},\"name\":\"ElementUpdateName\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\" ,\"attributes\": {\"Life\" : 50, \"Happiness\" : 50, \"Fed\" : 50}}";
		

		ElementTO toBeUpdated = this.jsonMapper.readValue(JsonToUpdate, ElementTO.class);
		toBeUpdated.setId(actualFullEntity.getPlaygroundAndID().getId());
		this.restTemplate.put(this.url + "/{userPlayground}/{email}/{playground}/{id} ", toBeUpdated, PLAYGROUND_NAME,
				MANAGER_EMAIL, PLAYGROUND_NAME, actualFullEntity.getPlaygroundAndID().getId());

	}
	@Test(expected=Exception.class)
	public void testUpdateElementIdFail() throws Exception {
		
		String entityJson = "{\"creatorPlayground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" ,"
				+ "\"x\":0,\"y\":0 ,\"name\":\"Test\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\"}";
		
		ElementEntity entity = this.jsonMapper.readValue(entityJson, ElementEntity.class);
		ElementEntity actualFullEntity = this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity);


		String updatedEntityJson = "{\"creatorPlayground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" ,"
				+ "\"name\":\"Test\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\",\"id\":\"1234a\"}";

		ElementTO toForPut = this.jsonMapper.readValue(updatedEntityJson, ElementTO.class);
		
		this.restTemplate.put(this.url + "/{userPlayground}/{email}/{playground}/{id} ", toForPut, PLAYGROUND_NAME,
				MANAGER_EMAIL, PLAYGROUND_NAME, actualFullEntity.getPlaygroundAndID().getId());


	}
	@Test
	public void testGetElementByPlayerSuccessfully() throws Exception {
	
		String entityJson = "{\"creatorPlayground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" ,"
				+ "\"x\":0,\"y\":0 ,\"name\":\"Test\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\"}";
		
		ElementEntity entity = this.jsonMapper.readValue(entityJson, ElementEntity.class);
		ElementEntity actualFullEntity = this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity);
		ElementTO actualElement = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/{playground}/{id}", ElementTO.class,
				PLAYGROUND_NAME,PLAYER_EMAIL,PLAYGROUND_NAME, actualFullEntity.getPlaygroundAndID().getId());
		assertThat(actualElement).isNotNull().extracting("playground", "id").containsExactly(PLAYGROUND_NAME, actualFullEntity.getPlaygroundAndID().getId());
	}

	@Test(expected = Exception.class)
	public void testGetElementWithWrongIDFailed() throws Exception {
		String entityJson = "{\"creatorPlayground\":\"TA\",\"creatorEmail\":\"kroyzman@gmail.com\" ,"
				+ "\"x\":0,\"y\":0 ,\"name\":\"Test\","
				+ "\"expirationDate\":null,\"type\":\"Tamagotchi\"}";
		
		ElementEntity entity = this.jsonMapper.readValue(entityJson, ElementEntity.class);
		ElementEntity actualFullEntity = this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity);
		
		this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/{playground}/{id}", ElementTO.class,
				PLAYGROUND_NAME,PLAYER_EMAIL,PLAYGROUND_NAME, 123);
	}

	
	
//get all elements
	@Test
	public void testGetAllElementsByPlayerFilteredExpDate() throws Exception {
		String entityJson1 = "{\"creatorEmail\": \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson2 = "{\"creatorEmail\" :\"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson3 = "{\"creatorEmail\" : \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";

		ElementEntity entity1 = this.jsonMapper.readValue(entityJson1, ElementEntity.class);
		entity1.setExpirationDate(formatter.parse("1-1-2020"));//
		
		ElementEntity entity2 = this.jsonMapper.readValue(entityJson2, ElementEntity.class);
		entity2.setExpirationDate(null);
		
		ElementEntity entity3 = this.jsonMapper.readValue(entityJson3, ElementEntity.class);
		entity3.setExpirationDate(formatter.parse("1-1-2018"));

		this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity1);
		this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity2);
		this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity3);

		ElementTO[] actualElements = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/all",
				ElementTO[].class, PLAYGROUND_NAME, PLAYER_EMAIL);


		
		assertThat(actualElements).isNotNull().hasSize(2);
	}
	@Test
	public void testExpiredElementDetailsDisplayedSuccessfullyByManager() throws Exception {
		String entityJson1 = "{\"creatorEmail\": \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson2 = "{\"creatorEmail\" :\"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson3 = "{\"creatorEmail\" : \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";

		ElementEntity entity1 = this.jsonMapper.readValue(entityJson1, ElementEntity.class);
		entity1.setExpirationDate(formatter.parse("1-1-2020"));//
		
		ElementEntity entity2 = this.jsonMapper.readValue(entityJson2, ElementEntity.class);
		entity2.setExpirationDate(null);
		
		ElementEntity entity3 = this.jsonMapper.readValue(entityJson3, ElementEntity.class);
		entity3.setExpirationDate(formatter.parse("1-1-2018"));

		entity1=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity1);
		entity2=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity2);
		entity3=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity3);
		

		ElementTO actualElement = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/{playground}/{id}", ElementTO.class,
				PLAYGROUND_NAME,PLAYER_EMAIL,PLAYGROUND_NAME, entity3.getPlaygroundAndID().getId());
		assertThat(actualElement).isNotNull().extracting("playground", "id").containsExactly(PLAYGROUND_NAME, entity3.getPlaygroundAndID().getId());
	
	}
	@Test
	public void testShowElementsWithPaginationSuccessfully() throws Exception {
		String entityJson1 = "{\"creatorEmail\": \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson2 = "{\"creatorEmail\" :\"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson3 = "{\"creatorEmail\" : \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";

		ElementEntity entity1 = this.jsonMapper.readValue(entityJson1, ElementEntity.class);
		entity1.setExpirationDate(formatter.parse("1-1-2020"));//
		
		ElementEntity entity2 = this.jsonMapper.readValue(entityJson2, ElementEntity.class);
		entity2.setExpirationDate(null);
		
		ElementEntity entity3 = this.jsonMapper.readValue(entityJson3, ElementEntity.class);
		entity3.setExpirationDate(formatter.parse("1-1-2018"));

		entity1=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity1);
		entity2=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity2);
		entity3=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity3);
		
		ElementTO[] actualElements = this.restTemplate.getForObject(
				this.url + "/{userPlayground}/{email}/all?size={size}&page={page}", ElementTO[].class, PLAYGROUND_NAME,
				MANAGER_EMAIL, 2, 1);
		assertThat(actualElements).isNotNull().hasSize(1);
	}

	@Test(expected = Exception.class)
	public void testGetMessageUsingInvalidPageNumberFail() {
		ElementTO[] actualElements = this.restTemplate.getForObject(
				this.url + "/{userPlayground}/{email}/all?page={page}", ElementTO[].class, PLAYGROUND_NAME,
				MANAGER_EMAIL, -1);
	}
	@Test
	public void testGetAllElementsClosestToSpecificLocationByDistanceSuccessfully() throws Exception {

		String entityJson1 = "{\"creatorEmail\": \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson2 = "{\"creatorEmail\" :\"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson3 = "{\"creatorEmail\" : \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";

		ElementEntity entity1 = this.jsonMapper.readValue(entityJson1, ElementEntity.class);
		entity1.setExpirationDate(formatter.parse("1-1-2020"));//
		
		ElementEntity entity2 = this.jsonMapper.readValue(entityJson2, ElementEntity.class);
		entity2.setExpirationDate(null);
		
		ElementEntity entity3 = this.jsonMapper.readValue(entityJson3, ElementEntity.class);
		entity3.setExpirationDate(formatter.parse("1-1-2018"));
		
		entity1.setX(5d);
		entity1.setY(5d);
		entity2.setX(1d);
		entity2.setY(1d);
		entity3.setX(10d);
		entity3.setY(10d);
		
		
		entity1=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity1);
		entity2=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity2);
		entity3=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity3);
		

		ElementTO[] rv = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}", ElementTO[].class,PLAYGROUND_NAME,MANAGER_EMAIL, 0, 0,
				5);

		assertThat(rv).isNotNull().hasSize(2);
	}
	@Test
	public void testGetAllElementsClosestToSpecificLocationByDistanceEmptySuccessfully() throws Exception {

		String entityJson1 = "{\"creatorEmail\": \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson2 = "{\"creatorEmail\" :\"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson3 = "{\"creatorEmail\" : \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";

		ElementEntity entity1 = this.jsonMapper.readValue(entityJson1, ElementEntity.class);
		entity1.setExpirationDate(formatter.parse("1-1-2020"));//
		
		ElementEntity entity2 = this.jsonMapper.readValue(entityJson2, ElementEntity.class);
		entity2.setExpirationDate(null);
		
		ElementEntity entity3 = this.jsonMapper.readValue(entityJson3, ElementEntity.class);
		entity3.setExpirationDate(formatter.parse("1-1-2018"));
		
		entity1.setX(5d);
		entity1.setY(5d);
		entity2.setX(1d);
		entity2.setY(1d);
		entity3.setX(10d);
		entity3.setY(10d);
		
		
		entity1=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity1);
		entity2=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity2);
		entity3=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity3);
		

		ElementTO[] rv = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}", ElementTO[].class,PLAYGROUND_NAME,MANAGER_EMAIL, 0, 0,
				0);

		assertThat(rv).isNotNull().hasSize(0);
	}

	@Test(expected = Exception.class)
	public void testGetAllElementsInGivenIncorrectRadiusFails() throws Exception {
	
		System.err.println(this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}", ElementTO[].class,PLAYGROUND_NAME,MANAGER_EMAIL, 0, 0,-1));
	}

	
	@Test
	public void getElementByAttributeAndValueSuccessfully() throws Exception {

		String attributeName = "Name";
		String value = "Test Element";
		String entityJson1 = "{\"name\" : \"Test Element\",\"creatorEmail\": \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson2 = "{\"name\" : \"Test Element\",\"creatorEmail\" :\"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson3 = "{\"name\" : \"Test Element2\",\"creatorEmail\" : \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";

		ElementEntity entity1 = this.jsonMapper.readValue(entityJson1, ElementEntity.class);
		ElementEntity entity2 = this.jsonMapper.readValue(entityJson2, ElementEntity.class);
		ElementEntity entity3 = this.jsonMapper.readValue(entityJson3, ElementEntity.class);
	

		entity1=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity1);
		entity2=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity2);
		entity3=this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity3);
		
		
		ElementTO[] rElements = this.restTemplate.getForObject(
				this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", ElementTO[].class,
				PLAYGROUND_NAME, MANAGER_EMAIL, attributeName, value);


		assertThat(rElements).isNotNull().hasSize(2);
		assertThat(rElements[0]).isNotNull().extracting("Name").contains("Test Element");
	}

	@Test
	public void testGetElementByAttributeAndValueSuccessfullyEmpty() throws Exception {
		ElementTO[] rElements = this.restTemplate.getForObject(
				this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", ElementTO[].class,
				PLAYGROUND_NAME, MANAGER_EMAIL, "Name", "hello");


		assertThat(rElements).isNotNull().hasSize(0);
	}	@Test
	public void getElementByAttributeAndValueSuccessfullyPlayer() throws Exception {

		String entityJson1 = "{\"name\" : \"Test Element\",\"creatorEmail\": \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson2 = "{\"name\" : \"Test Element\",\"creatorEmail\" :\"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";
		String entityJson3 = "{\"name\" : \"Test Element\",\"creatorEmail\" : \"niko@gmail.com\",\"type\":\"Tamagotchi\",\"creatorPlayground\":\"TA\"}";

		ElementEntity entity1 = this.jsonMapper.readValue(entityJson1, ElementEntity.class);
		entity1.setExpirationDate(formatter.parse("1-1-2020"));//
		
		ElementEntity entity2 = this.jsonMapper.readValue(entityJson2, ElementEntity.class);
		entity2.setExpirationDate(null);
		
		ElementEntity entity3 = this.jsonMapper.readValue(entityJson3, ElementEntity.class);
		entity3.setExpirationDate(formatter.parse("1-1-2018"));

		this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity1);
		this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity2);
		this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, entity3);
		
		ElementTO[] rElements = this.restTemplate.getForObject(
				this.url + "/{userPlayground}/{email}/search/{attributeName}/{value}", ElementTO[].class,
				PLAYGROUND_NAME, PLAYER_EMAIL, "Name", "Test Element");
		
		assertThat(rElements).isNotNull().hasSize(2);
	}
}
