package playground.test;

import static org.assertj.core.api.Assertions.assertThat;

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


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes = Application.class)
public class ActivityTests {

	@Autowired
	private  ActivityService actService;
	
	private RestTemplate restTemplate;
	private String url;
	
	@LocalServerPort
	private int port;
	
	private ObjectMapper jacksonMaper;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/activity";
		
		//// Jackson init
		this.jacksonMaper = new ObjectMapper();
	}
	
	@Before
	public void setup () {
		
	}
	@After
	public void teardown() {
		
	}
	
	@Test
	public void testServerInitializesProperly() {
		
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
