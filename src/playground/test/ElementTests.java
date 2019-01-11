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
	
	@Test
	public void testGetElementByPlayerSuccessfully() throws Exception {
	
		String id = "123";
	
		
		ElementEntity addElement = new ElementEntity();
		addElement.setPlaygroundAndID(new ElementKey(id,PLAYGROUND_NAME));
		addElement.setCreationDate(new Date());
		addElement.setExpirationDate(new Date(2020,1,1));
		addElement.setType("message_board");
		//addElement.setId(id);
		ElementEntity rv = this.elementService.addNewElement(PLAYGROUND_NAME, MANAGER_EMAIL, addElement);
		
		ElementTO actualElement = this.restTemplate.getForObject(this.url + "/{playground}/{id}", ElementTO.class,
				PLAYGROUND_NAME, rv.getPlaygroundAndID().getId());
		assertThat(actualElement).isNotNull().extracting("playground", "id").containsExactly(PLAYGROUND_NAME, rv.getPlaygroundAndID().getId());
	}

	@Test(expected = Exception.class)
	public void testGetElementFailed() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		String id = "123";
		
		
		this.restTemplate.getForObject(this.url + "/{playground}/{id}", ElementTO.class, playground, id);
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

//				When I Get playground/elements/2019A.shir/dorc@gmail.com/all
//
		ElementTO[] actualElements = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/all",
				ElementTO[].class, PLAYGROUND_NAME, PLAYER_EMAIL);

//				Then the response status is 200 and the body is an array of 2 elements
		
		System.err.println(actualElements[0] + "-------" + actualElements[1]);
		assertThat(actualElements).isNotNull().hasSize(2);
	}

	@Test
	public void testGetAllElementsClosestToSpecificLocationByDistanceSuccessfully() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		
		
		ElementEntity e1 = new ElementEntity();
		e1.setPlaygroundAndID("Maayan");
		e1.setId("123");
		e1.setCreatorEmail("benny@ac.il");
		e1.setX(1d);
		e1.setY(3d);
		e1.setName("Tamagotchi");
		e1.setCreationDate(new Date());
		e1.setExpirationDate(new Date(2019, 1, 20));
		e1.setType("Pet");
		e1.setCreatorPlayground("TA");

		ElementEntity e2 = new ElementEntity();
		e2.setPlaygroundAndID("Maayan");
		e2.setCreatorEmail("benny@ac.il");
		e2.setId("124");
		e2.setX(2d);
		e2.setY(3d);
		e2.setName("Message Board");
		e2.setCreationDate(new Date());
		e2.setExpirationDate(new Date(2019, 2, 24));
		e2.setType("Pet");
		e2.setCreatorPlayground("TA");

		ElementEntity e3 = new ElementEntity();
		e3.setCreatorEmail("benny@ac.il");
		e3.setPlaygroundAndID("TA");
		e3.setId("125");
		e3.setX(10d);
		e3.setY(10d);

		ElementEntity e4 = new ElementEntity();
		e4.setPlaygroundAndID("TA");
		e4.setCreatorEmail("benny@ac.il");
		e4.setId("126");
		e4.setX(15d);
		e4.setY(15d);

		ElementEntity e5 = new ElementEntity();
		e5.setPlaygroundAndID("TA");
		e5.setCreatorEmail("benny@ac.il");
		e5.setId("127");
		e5.setX(20d);
		e5.setY(20d);

		Stream.of(e1, e2, e3, e4, e5).forEach(t -> {
			try {
				this.elementService.addNewElement(playground, email, t);
			} catch (ElementAlreadyExistsException e) {

				e.printStackTrace();
			}
		});

		ElementTO[] rv = this.restTemplate.getForObject(this.url + "/near/{x}/{y}/{distance}", ElementTO[].class, 0, 0,
				4);

		assertThat(rv).isNotNull().hasSize(2);
	}

	@Test(expected = Exception.class)
	public void testGetAllElementsInGivenRadiusFails() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
	
		
		this.restTemplate.getForObject(this.url + "/near/{x}/{y}/{distance}", ElementTO[].class, 1, 1, -1);
	}

	@Test
	public void getElementByAttributeAndValueSuccessfully() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		
		
		ElementEntity e1 = new ElementEntity();
		e1.setPlaygroundAndID("Maayan");
		e1.setId("123");
		e1.setX(0d);
		e1.setY(0d);
		e1.setName("Tamagotchi");
		e1.setCreationDate(new Date());
		e1.setExpirationDate(new Date(2020, 1, 20));
		e1.setType("Pet");
		e1.addAttribute("color", "red");

		ElementEntity e2 = new ElementEntity();
		e2.setPlaygroundAndID("Maayan");
		e2.setId("124");
		e2.setName("Message Board");
		e2.setX(0d);
		e2.setY(0d);
		e2.setCreationDate(new Date());
		e2.setExpirationDate(new Date(2020, 1, 22));
		e2.setType("Pet");
		e2.addAttribute("color", "blue");
		ElementEntity e3 = new ElementEntity();
		e3.setPlaygroundAndID("TA");
		e3.setId("125");
		e3.setName("test");
		e3.addAttribute("color", "blue");

		Stream.of(e1, e2, e3).forEach(t -> {
			try {
				this.elementService.addNewElement(playground, email, t);
			} catch (ElementAlreadyExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		ElementTO[] actualElements = this.restTemplate.getForObject(this.url + "/search/{attributeName}/{value}",
				ElementTO[].class, "color", "blue");
		assertThat(actualElements).isNotNull().hasSize(2);
	}

	@Test
	public void testGetElementByAttributeAndValueSuccessfullyEmpty() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		
		
		ElementTO[] actualElements = this.restTemplate.getForObject(this.url + "/search/{attributeName}/{value}",
				ElementTO[].class, "playground", "Maayan");
		assertThat(actualElements).isNotNull().hasSize(0);
	}

	@Test
	public void testShowElementsUsingPaginationSuccessFully() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("Color", 1);

		ElementEntity element1 = new ElementEntity("TA", "123", 1.0, 1.0, "element1", new Date(), new Date(2019, 1, 20),
				"pet", attributes, "Nikos Vertis", "niko@ac.il");
		this.elementService.addNewElement(playground, email, element1);

		ElementEntity element2 = new ElementEntity("TA", "124", 2.0, 2.0, "element2", new Date(), new Date(2019, 1, 21),
				"pet", attributes, "Maayan Boaron", "maayan@ac.il");
		this.elementService.addNewElement(playground, email, element2);

		ElementEntity element3 = new ElementEntity("TA", "125", 3.0, 3.0, "element3", new Date(), new Date(2019, 1, 22),
				"pet", attributes, "Idan Haham", "idan@ac.il");
		this.elementService.addNewElement(playground, email, element3);

		ElementTO[] elements = this.restTemplate.getForObject(this.url + "/all?size={size}&page={page}",
				ElementTO[].class, 2, 0);
		assertThat(elements).isNotNull().hasSize(2);
	}

	@Test(expected = Exception.class)
	public void testShowElementsUsingPaginationFailsWithIncorrectParametres() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("Color", 1);

		ElementEntity element1 = new ElementEntity("TA", "123", 1.0, 1.0, "element1", new Date(), new Date(2019, 1, 20),
				"pet", attributes, "Nikos Vertis", "niko@ac.il");
		this.elementService.addNewElement(playground, email, element1);

		ElementEntity element2 = new ElementEntity("TA", "124", 2.0, 2.0, "element2", new Date(), new Date(2019, 1, 21),
				"pet", attributes, "Maayan Boaron", "maayan@ac.il");
		this.elementService.addNewElement(playground, email, element2);

		ElementEntity element3 = new ElementEntity("TA", "125", 3.0, 3.0, "element3", new Date(), new Date(2019, 1, 22),
				"pet", attributes, "Idan Haham", "idan@ac.il");
		this.elementService.addNewElement(playground, email, element3);

		ElementTO[] elements = this.restTemplate.getForObject(this.url + "/all?size={size}&page={page}",
				ElementTO[].class, 5, -1);
		assertThat(elements).isNotNull().hasSize(2);
	}
}
