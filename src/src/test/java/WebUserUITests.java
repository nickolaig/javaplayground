package src.test.java;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import src.main.java.playground.layout.UserTO;
import src.main.java.playground.logic.UserEntity;
import src.main.java.playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
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
	
	@Test
	public void initlizeServer() {
		
	}
	
//	@Test
//	public void testUserLoginSuccessfully() {
//		String email = "benny@ac.il";
//		String playground = "TA";
//		
//		//Given database contains {email : "benny@ac.il" , playground = "TA"}
//		this.userService.addNewUser(new UserEntity(email, playground, "Idan", "Avatar", "Player", 1));
//		
//		// When I invoke GET this.url + "/login/TA/benny@ac.il"
//		UserTO actualUser = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class, playground, email);
//		
//		assertThat(actualUser)
//		.isNotNull()
//		.extracting("playground")
//		.containsExactly(playground);
//		
//	}
}
