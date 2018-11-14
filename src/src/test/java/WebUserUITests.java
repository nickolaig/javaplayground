package src.test.java;

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

import src.main.java.playground.Application;
import src.main.java.playground.layout.UserTO;
import src.main.java.playground.logic.UserEntity;
import src.main.java.playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes = Application.class)
public class WebUserUITests {
	@LocalServerPort
	private int port;
	
	private String url;
	
	private RestTemplate restTemplate;
	@Autowired
	private UserService userService;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/users";
		System.err.println(this.url);
	}
	
	@Before
	public void setup() {
		
	}

	@After
	public void teardown() {
		this.userService.cleanup();
	}
	
	@Test
	public void testUserLoginSuccessfully() {
		String email = "benny@ac.il";
		String playground = "TA";
		
		
		//Given database contains {email : "benny@ac.il" , playground = "TA"}
		this.userService.addNewUser(new UserEntity(email, "TA", "Idan", "Avatar", "Player", 1));
		
		// When I invoke GET this.url + "/login/TA/benny@ac.il"
		UserTO actualUser = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class, playground, email);
		
		assertThat(actualUser)
		.isNotNull()
		.extracting("email", "playground")
		.containsExactly(email, playground);
	}
	
	@Test(expected=Exception.class)
	public void testUserLoginWithIncorrectEmail() {
		String email = "benny@gmail.com";
		String playground = "TA";
		
		// When I invoke GET this.url + "/login/TA/benny@ac.il"
				UserTO actualUser = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class, playground, email);
	}
}
