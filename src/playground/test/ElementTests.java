package playground.test;

import static org.assertj.core.api.Assertions.assertThat;

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
import playground.logic.ElementService;
import playground.logic.ElementTO;
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
	@Autowired
	private ElementService elementService;
	@Autowired
	private UserService userService;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements/TA/benny@ac.il";

		// Jackson init
		this.jsonMapper = new ObjectMapper();
	}

	@Before
	public void setup() throws Exception {
		String creatorPlayground = "TA";
		String creatorEmail = "benny@ac.il";
		UserKey userKey = new UserKey(creatorEmail, creatorPlayground);

		// TODO: persisting data --> this lines not needed
		// create new user and validate his account
		UserEntity currentUser = userService.addNewUser(new UserEntity(userKey, "Nadi", "Any", "Manager", 0L));
		currentUser.setIsValidate(true);
		userService.updateUser(currentUser, userKey);
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
	public void testCreateElementSuccessfully() throws Exception {
		String playground = "TA";
		String id = "32167";
		String name = "Tamagotchi";
		String creatorPlayground = "TA";
		String creatorEmail = "benny@ac.il";
		

		ElementTO newElement = new ElementTO(playground, id, name, creatorPlayground, creatorEmail);
		ElementTO rv = this.restTemplate.postForObject(this.url, newElement, ElementTO.class);

		assertThat(rv.getPlayground()).isEqualTo(playground);
		assertThat(rv.getId()).isEqualTo(id);

		ElementEntity expectedEntityResult = newElement.toEntity();

		assertThat(this.elementService.getElementById(creatorPlayground, creatorEmail, playground, id)).isNotNull().usingComparator((e1, e2) -> {
			int rvalue = (e1.getPlayground() + e1.getId()).compareTo(e2.getPlayground() + e2.getId());
			if (rvalue == 0) {
				rvalue = new Double(e1.getX()).compareTo(e2.getX());
				if (rvalue == 0) {
					rvalue = new Double(e1.getY()).compareTo(e2.getY());
				}
			}
			return rvalue;
		}).isEqualTo(expectedEntityResult);
	}

	@Test(expected = Exception.class)
	public void testCreateElementFailsDuplicateElement() throws Exception {
		String playground = "TA";
		String id = "32167";
		String name = "Tamagotchi";
		String creatorPlayground = "TA";
		String creatorEmail = "benny@ac.il";
		

		ElementTO newElement = new ElementTO(playground, id, name, creatorPlayground, creatorEmail);
		ElementTO rv = this.restTemplate.postForObject(this.url, newElement, ElementTO.class);
		ElementTO rv2 = this.restTemplate.postForObject(this.url, newElement, ElementTO.class);

	}

	// Update Element
	@Test
	public void testUpdateElementSuccessfully() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		String id = "123";
		
		
		String entityJson = "{\"id\":\"123\", \"playground\":\"TA\",\"name\":\"Tamagotchi\",\"creatorPlayground\":\"TA\",\"creatorEmail\":\"benny@ac.il\"}";

		ElementEntity existingElement = this.jsonMapper.readValue(entityJson, ElementEntity.class);

		this.elementService.addNewElement(playground, email, existingElement);

		String elementToString = "{\"id\":\"123\", \"playground\":\"TA\",\"name\":\"Tamagucci\",\"creatorPlayground\":\"TA\",\"creatorEmail\":\"newbenny@ac.il\"}";
		ElementTO updatedElement = this.jsonMapper.readValue(elementToString, ElementTO.class);

		this.restTemplate.put(this.url + "/{playground}/{id}", updatedElement, playground, id);

		ElementEntity actualEntity = this.elementService.getElementById(playground, email, playground, id);

		assertThat(actualEntity).isNotNull().extracting("playground", "id", "name", "creatorEmail")
				.containsExactly(playground, id, "Tamagucci", "newbenny@ac.il");

	}

	@Test(expected = Exception.class)
	public void testUpdateElementWithNonExistingElement() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		String id = "123";
		
		
		String entityJson = "{\"id\":\"123\", \"playground\":\"TA\",\"name\":\"Tamagotchi\",\"creatorPlayground\":\"TA\",\"creatorEmail\":\"benny@ac.il\"}";
		ElementTO newEntity = this.jsonMapper.readValue(entityJson, ElementTO.class);

		this.restTemplate.put(this.url + "/{playground}/{id}", newEntity, playground, id);
	}

	@Test
	public void testGetElementSuccessfully() throws Exception {
		String playground = "TA";
		String id = "123";
		String email = "benny@ac.il";
		
		ElementEntity addElement = new ElementEntity();
		addElement.setPlayground(playground);
		addElement.setId(id);
		this.elementService.addNewElement(playground, email, addElement);
		ElementTO actualElement = this.restTemplate.getForObject(this.url + "/{playground}/{id}", ElementTO.class,
				playground, id);
		assertThat(actualElement).isNotNull().extracting("playground", "id").containsExactly(playground, id);
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
	public void testGetAllElementsSuccess() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		
		
		Stream.of("1", "2", "3", "4", "5").map(ElementEntity::new).forEach(t -> {
			try {
				this.elementService.addNewElement(playground, email, t);
			} catch (ElementAlreadyExistsException e) {
				e.printStackTrace();
			}
		});
		ElementTO[] elements = this.restTemplate.getForObject(this.url + "/all", ElementTO[].class);
		System.out.println("ASDASDASDASDSA\n" + elements);
		assertThat(elements).isNotNull().hasSize(5);
	}

	@Test
	public void testGetAllElementsClosestToSpecificLocationByDistanceSuccessfully() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		
		
		ElementEntity e1 = new ElementEntity();
		e1.setPlayground("Maayan");
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
		e2.setPlayground("Maayan");
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
		e3.setPlayground("TA");
		e3.setId("125");
		e3.setX(10d);
		e3.setY(10d);

		ElementEntity e4 = new ElementEntity();
		e4.setPlayground("TA");
		e4.setCreatorEmail("benny@ac.il");
		e4.setId("126");
		e4.setX(15d);
		e4.setY(15d);

		ElementEntity e5 = new ElementEntity();
		e5.setPlayground("TA");
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
		e1.setPlayground("Maayan");
		e1.setId("123");
		e1.setX(0d);
		e1.setY(0d);
		e1.setName("Tamagotchi");
		e1.setCreationDate(new Date());
		e1.setExpirationDate(new Date(2020, 1, 20));
		e1.setType("Pet");
		e1.addAttribute("color", "red");

		ElementEntity e2 = new ElementEntity();
		e2.setPlayground("Maayan");
		e2.setId("124");
		e2.setName("Message Board");
		e2.setX(0d);
		e2.setY(0d);
		e2.setCreationDate(new Date());
		e2.setExpirationDate(new Date(2020, 1, 22));
		e2.setType("Pet");
		e2.addAttribute("color", "blue");
		ElementEntity e3 = new ElementEntity();
		e3.setPlayground("TA");
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
