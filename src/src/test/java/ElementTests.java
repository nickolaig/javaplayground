package src.test.java;

import static org.assertj.core.api.Assertions.assertThat;

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

import src.main.java.playground.Application;
import src.main.java.playground.logic.ElementEntity;
import src.main.java.playground.logic.ElementService;
import src.main.java.playground.logic.ElementTO;
import src.main.java.playground.logic.Location;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes = Application.class)
public class ElementTests {
	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	private ObjectMapper jsonMapper;
	@Autowired
	private ElementService elementService;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements/TA/benny@ac.il";
		
		// Jackson init
		this.jsonMapper = new ObjectMapper();
	}
	
	@Before
	public void setup() {
		
	}

	@After
	public void teardown() {
		this.elementService.cleanup();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
		
	}
	
	//Create new element
	@Test
	public void testCreateElementSuccessfully()throws Exception{
		String playground = "TA";
		String id = "32167";
		String name ="Tamagotchi";
		String creatorName="TA";
		String creatorEmail="benny@ac.il";
		double x = 123.1;
		double y = 123.1;
		
		ElementTO newElement = new ElementTO(playground, id, name, creatorName, creatorEmail);
		newElement.setLocation(new Location(x, y));
		
		ElementTO rv = this.restTemplate.postForObject(this.url, newElement, ElementTO.class);
		
		assertThat(rv.getPlayground()).isEqualTo(playground);
		assertThat(rv.getId()).isEqualTo(id);
		
		ElementEntity expectedEntityResult = newElement.toEntity();

		assertThat(this.elementService.getElementById(playground, id))
		.isNotNull()
		.usingComparator((e1,e2)->{
			int rvalue = (e1.getPlayground() + e1.getId()).compareTo(e2.getPlayground() + e2.getId());
			if (rvalue == 0) {
				rvalue = new Double(e1.getLocation().getX()).compareTo(e2.getLocation().getX());
				if (rvalue == 0)
				{
					rvalue = new Double(e1.getLocation().getY()).compareTo(e2.getLocation().getY());
				}
			}
			return rvalue;
		})
		.isEqualTo(expectedEntityResult);
	}
	
	//Update Element
	@Test
	public void  testUpdateElementSuccessfully()throws Exception{
		String playground = "TA";
		String id = "123";
		
		String entityJson =
				"{\"id\":\"123\", \"playground\":\"TA\",\"name\":\"Tamagotchi\",\"creatorPlayground\":\"TA\",\"creatorEmail\":\"benny@ac.il\"}";

		ElementEntity existingElement = this.jsonMapper.readValue(entityJson, ElementEntity.class);
		
		this.elementService.addNewElement(existingElement);
		

		String elementToString = "{\"id\":\"123\", \"playground\":\"TA\",\"name\":\"Tamagucci\",\"creatorPlayground\":\"TA\",\"creatorEmail\":\"newbenny@ac.il\"}";
		ElementTO updatedElement = this.jsonMapper.readValue(elementToString, ElementTO.class);
		
		this.restTemplate.put(this.url + "/{playground}/{id}", updatedElement, playground, id);
		
		ElementEntity actualEntity = this.elementService.getElementById(playground, id);
		

		assertThat(actualEntity)
				.isNotNull()
				.extracting("playground", "id", "name", "creatorEmail")
				.containsExactly(playground, id, "Tamagucci", "newbenny@ac.il");
		
	}
	
	@Test(expected=Exception.class)
	public void testUpdateElementWithNonExistingElement() throws Exception{
		String playground = "TA";
		String id = "123";
		
		String entityJson =
				"{\"id\":\"123\", \"playground\":\"TA\",\"name\":\"Tamagotchi\",\"creatorPlayground\":\"TA\",\"creatorEmail\":\"benny@ac.il\"}";
		ElementTO newEntity = this.jsonMapper.readValue(entityJson, ElementTO.class);
				
		this.restTemplate.put(this.url + "/{playground}/{id}", newEntity, playground, id);
	}
	

	@Test
	public void  testGetSpecificElementSuccessfully() throws Exception{
		String playground = "TA";
		String id = "123";
		
		ElementEntity addElement = new ElementEntity();
		addElement.setPlayground(playground);
		addElement.setId(id);
		this.elementService.addNewElement(addElement);
		ElementTO actualElement = this.restTemplate.getForObject(this.url + "/{playground}/{id}",ElementTO.class, playground, id); 
		assertThat(actualElement)
				.isNotNull()
				.extracting("playground", "id")
				.containsExactly(playground, id);
	}
	
	@Test(expected=Exception.class)
	public void testGetSpecificMessageWithInvalidName() throws Exception{
		String playground = "TA";
		String id = "123";

		this.restTemplate.getForObject(this.url + "/{playground}/{id}",
				ElementTO.class, playground, id); 
	}
	
//get all elements
	@Test
	public void testGetAllElementsSuccess()throws Exception {
		Stream.of("1", "2", "3","4","5").map(ElementEntity::new)
				.forEach(this.elementService::addNewElement);
		ElementTO[] elements = this.restTemplate.getForObject(this.url + "/all", ElementTO[].class);
		assertThat(elements)
				.isNotNull()
				.hasSize(5);
	}

	@Test
	public void testGetAllElementsInGivenRadiusSuccessfully() throws Exception{
		ElementEntity e1 = new ElementEntity();
		e1.setPlayground("TA");
		e1.setId("123");
		e1.setCreatorEmail("benny@ac.il");
		e1.setLocation(new Location(0,0));
		ElementEntity e2 = new ElementEntity();
		e2.setPlayground("TA");
		e2.setCreatorEmail("benny@ac.il");
		e2.setId("124");
		e2.setLocation(new Location(1,1));
		ElementEntity e3 = new ElementEntity();
		e3.setCreatorEmail("benny@ac.il");
		e3.setPlayground("TA");
		e3.setId("125");
		e3.setLocation(new Location(10,10));
		ElementEntity e4 = new ElementEntity();
		e4.setPlayground("TA");
		e4.setCreatorEmail("benny@ac.il");
		e4.setId("126");
		e4.setLocation(new Location(15,15));

		ElementEntity e5 = new ElementEntity();
		e5.setPlayground("TA");
		e5.setCreatorEmail("benny@ac.il");
		e5.setId("127");
		e5.setLocation(new Location(20,20));
		
		Stream.of(e1, e2, e3,e4,e5)
				.forEach(this.elementService::addNewElement);
		
		ElementTO[] rv = this.restTemplate.getForObject(this.url + "/near/{x}/{y}/{distance}", ElementTO[].class, 0, 0, 4);

		assertThat(rv)
				.isNotNull()
				.hasSize(2);
	}
	
	@Test(expected=Exception.class)
	public void testGetAllElementsInGivenRadiusFails() throws Exception{
		this.restTemplate.getForObject(this.url + "/near/{x}/{y}/{distance}", ElementTO[].class, 1, 1, -1);
	}


//	@Test
//	public void   getElementByAttributeAndValueSuccessfully() throws Exception{
//		ElementEntity e1 = new ElementEntity();
//		e1.setPlayground("TA");
//		e1.setId("123");
//		e1.setName("test1");
//		ElementEntity e2 = new ElementEntity();
//		e2.setPlayground("TA");
//		e2.setId("124");
//		e2.setName("test");
//		ElementEntity e3 = new ElementEntity();
//		e3.setPlayground("TA");
//		e3.setId("125");
//		e3.setName("test");
//
//
//		Stream.of(e1, e2, e3)
//		.forEach(this.elementService::addNewElement);
//		
//		ElementTO[] actualElements = this.restTemplate.getForObject(this.url + "/search/{attributeName}/{value}", ElementTO[].class, "name", "test");
//		
//		assertThat(actualElements)
//				.isNotNull()
//				.hasSize(2);
//	}
//	


}
