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
import playground.logic.NewUserForm;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.UserTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class WebUserUITests {
	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	private UserService userService;
	private ObjectMapper jsonMapper;
	private static final String DEFAULT_PLAYGROUND = "TA";
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/users";
		this.jsonMapper = new ObjectMapper();
	}

	@Before
	public void setup() {

	}

	@After
	public void teardown() {
		this.userService.cleanup();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {

	}

	@Test
	public void testCreateUserSuccessfully() throws Exception {

		String email = "kroyzman546@gmail.com";
		String userName = "Nadav";
		String role = "Player";
		String avatar = "Niko";
		
		NewUserForm userForm = new NewUserForm(email, userName, role, avatar);

		/*
		 * When I POST /playground/users with Body: { "email": "kroyzman546@gmail.com",
		 * "userName": "Nadav", "role": "Player", "avatar": "loYodea" }
		 */
		UserTO actualReturnedUser = this.restTemplate.postForObject(this.url, userForm, UserTO.class);

		/*
		 * Then the response body contains a user with the
		 * “email”:”kroyzman546@gmail.com” and the database retrieves for the
		 * email=”kroyzman546@gmail.com” the user { "email": "kroyzman546@gmail.com",
		 * "playground": "Nadi", "userName": "Nadav", "avatar": "loYodea", "role":
		 * "Player", "points": 0 }
		 * 
		 */
		
		UserEntity expectedOutcome = new UserEntity(new UserKey(email, DEFAULT_PLAYGROUND), userName, avatar, role, 0L);
		
		assertThat(this.userService.getUserByEmailAndPlayground(new UserKey(email, DEFAULT_PLAYGROUND)))
		.isNotNull()
		.extracting("userEmailPlaygroundKey", "userName", "avatar", "role", "points").containsExactly(
				expectedOutcome.getUserEmailPlaygroundKey(), expectedOutcome.getUserName(),
				expectedOutcome.getAvatar(), expectedOutcome.getRole(), expectedOutcome.getPoints());
		
		assertThat(expectedOutcome).isNotNull().extracting("isValidate").contains(false);
	}
	
	@Test(expected = Exception.class)
	public void testFailToCreateUserWithNullEmail() {

		String email = null;
		String userName = "Nadav";
		String role = "Player";
		String avatar = "Niko";
		
	    
		NewUserForm thePostedNewUser = new NewUserForm(email, userName, avatar, role);

		this.restTemplate.postForObject(this.url, thePostedNewUser, UserTO.class);
	}

	@Test(expected = Exception.class)
	public void testCreateUserWithExistingUser() throws Exception {
		String email = "kroyzman546@ac.il";
		String userName = "Nadav";
		String role = "Player";
		String avatar = "Niko";
		
		NewUserForm userForm = new NewUserForm(email, userName, role, avatar);
		UserEntity existingUser = new UserEntity(new UserKey(email, DEFAULT_PLAYGROUND), "benny", "mashu", "Manager", 0L);

		// Given the server is up And the database contains a user with email
		// "kroyzman546@gmail.com" and playground "Nadi"
		userService.addNewUser(existingUser);

		// When
		UserTO returnedUser = this.restTemplate.postForObject(this.url, userForm, UserTO.class);
	}


	@Test
	public void testUserLoginSuccessfully() throws Exception {
		String email = "kroyzman546@ac.il";
		String userName = "Nadav";
		String role = "Player";
		String avatar = "Niko";

		// Given database contains {email : "benny@ac.il" , playground = "TA"}
		UserEntity currentUser = this.userService.addNewUser(new UserEntity(new UserKey(email, DEFAULT_PLAYGROUND), userName, avatar, role, 1L));
		
		this.userService.confirm(DEFAULT_PLAYGROUND, email, currentUser.getCode());
		
		// When I invoke GET this.url + "/login/TA/kroyzman546@ac.il"
		UserTO actualUser = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class,
				DEFAULT_PLAYGROUND, email);

		assertThat(actualUser).isNotNull().extracting("email", "playground").containsExactly(email, DEFAULT_PLAYGROUND);
	}

	@Test(expected = Exception.class)
	public void testUserLoginWithNonExistingUser() {
		String email = "benny@ac.il";

		UserTO actualUser = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class,
				DEFAULT_PLAYGROUND, email);
	}

	@Test
	public void testUserConfirmationCorrectly() throws Exception {
		
		String email = "kroyzman546@ac.il";
		String userName = "Nadav";
		String role = "Manager";
		String avatar = "Niko";

		int code;
		
		// Given database contains {email : "benny@ac.il" , playground = "TA"}
		code = this.userService.addNewUser(new UserEntity(new UserKey(email, DEFAULT_PLAYGROUND), userName, avatar, role, 0L)).getCode();

		// When I invoke GET this.url + "/confirm/TA/kroyzman546@ac.il/code"
		UserTO actualUser = this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}",
				UserTO.class, DEFAULT_PLAYGROUND, email, code);
		
		UserEntity userFromDB = userService.getUserByEmailAndPlayground(new UserKey(actualUser.getEmail(), actualUser.getPlayground()));
		
		assertThat(userFromDB).isNotNull().extracting("userEmailPlaygroundKey", "isValidate", "code")
				.containsExactly(new UserKey(email, DEFAULT_PLAYGROUND), true, code);
		
	}

	@Test(expected = Exception.class)
	public void testUserConfirmationWithIncorrectCode() throws Exception {
		String email = "kroyzman546@ac.il";
		String userName = "Nadav";
		String role = "Manager";
		String avatar = "Niko";

		// Given database contains {email : "benny@ac.il" , playground = "TA"}
		UserEntity user = this.userService.addNewUser(new UserEntity(new UserKey(email, DEFAULT_PLAYGROUND), "Maayan", "Avatar", "Manager", 0L));

		// When I invoke GET this.url + "/confirm/TA/benny@ac.il/123"
		UserTO actualUser = this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}",
				UserTO.class, DEFAULT_PLAYGROUND, email, user.getCode() + 1);
	}

	@Test
	public void testSelfUpdateUserDetailsSuccessfully() throws Exception {
		String email = "kroyzman546@ac.il";
		String userName = "Nadav";
		String role = "Player";
		String avatar = "Niko";
	
		// given
		
		//String entityJson = "{\"email\":\"benny@ac.il\", \"playground\":\"TA\", \"userName\":\"benny\", \"avatar\":\"loyodea\", \"role\":\"Manager\", \"points\":0}";
		//UserEntity oldUser = this.jsonMapper.readValue(entityJson, UserEntity.class);
		
		UserEntity oldUser = new UserEntity(new UserKey(email, DEFAULT_PLAYGROUND), userName, avatar, role, 5L);
		this.userService.addNewUser(oldUser);

		// when
		String userToString = "{\"email\":\"kroyzman546@ac.il\", \"playground\":\"TA\", \"userName\":\"benny\", \"avatar\":\"Niko\", \"role\":\"Manager\", \"points\":5}";
		UserTO to = this.jsonMapper.readValue(userToString, UserTO.class);

		this.restTemplate.put(this.url + "/{playground}/{email}", to, DEFAULT_PLAYGROUND, email);

		// then
		UserEntity actualEntity = this.userService.getUserByEmailAndPlayground(new UserKey(email, DEFAULT_PLAYGROUND));
		assertThat(actualEntity).isNotNull().extracting("userName", "role", "points").containsExactly("benny",
				"Manager", 0L);
	}
	
	@Test
	public void testTheManagerUpdateAnotherUserAccountSuccessfully() throws Exception {
		String email = "kroyzman546@ac.il";
		String userName = "Nadav";
		String role = "Manager";
		String avatar = "Niko";
		
		String otherEmail = "niko@ac.il";
		String otherUserName = "Niko";
		String otherRole = "Player";
		String otherAvatar = "Niko";
		long points = 10;
		
		UserEntity managerUser = userService.addNewUser(new UserEntity(new UserKey(email, DEFAULT_PLAYGROUND), userName, avatar, role, 0L));
		
		UserEntity otherUser = userService.addNewUser(new UserEntity(new UserKey(otherEmail, DEFAULT_PLAYGROUND), otherUserName, otherAvatar, otherRole, points));
		  
		String userToString = "{\"email\":\"niko@ac.il\", \"playground\":\"TA\", \"userName\":\"Niko\", \"avatar\":\"Niko\", \"role\":\"Player\", \"points\":5}";
		UserTO to = this.jsonMapper.readValue(userToString, UserTO.class);
		
		to.setEnabled(false);
		
		this.restTemplate.put(this.url + "/{playground}/{email}", to, DEFAULT_PLAYGROUND, email);
		
		
		UserEntity actualEntityInDb = this.userService.getUserByEmailAndPlayground(new UserKey(otherEmail, DEFAULT_PLAYGROUND));
		
		UserEntity expectedEntity = to.toEntity();
		
		assertThat(actualEntityInDb).isNotNull().extracting("points", "isEnabled").containsExactly(
				 5L, false);
				
	}
	
	@Test(expected=Exception.class)
	public void testPlayerUpdateAnotherAccountFailed() throws Exception {
		String email = "kroyzman546@ac.il";
		String userName = "Nadav";
		String role = "Player";
		String avatar = "Niko";
		
		String otherEmail = "niko@ac.il";
		String otherUserName = "Niko";
		String otherRole = "Player";
		String otherAvatar = "Niko";
		long points = 10;
		
		UserEntity playerUser = userService.addNewUser(new UserEntity(new UserKey(email, DEFAULT_PLAYGROUND), userName, avatar, role, 0L));
		
		UserEntity otherPlayerUser = userService.addNewUser(new UserEntity(new UserKey(otherEmail, DEFAULT_PLAYGROUND), otherUserName, otherAvatar, otherRole, points));
		  
		String userToString = "{\"email\":\"niko@ac.il\", \"playground\":\"TA\", \"userName\":\"Niko\", \"avatar\":\"Niko\", \"role\":\"Player\", \"points\":5}";
		UserTO to = this.jsonMapper.readValue(userToString, UserTO.class);
		
		this.restTemplate.put(this.url + "/{playground}/{email}", to, DEFAULT_PLAYGROUND, email);
				
	}
	
	@Test(expected = Exception.class)
	public void testUpdateWithNonExistingUser() throws Exception {
		String email = "benny@ac.il";
		String playground = "TA";
		
		// when
		String userToString = "{\"email\":\"benny@ac.il\", \"playground\":\"TA\", \"userName\":\"benny4k\", \"avatar\":\"loyodea\", \"role\":\"Player\", \"points\":5}";
		UserTO to = this.jsonMapper.readValue(userToString, UserTO.class);

		this.restTemplate.put(this.url + "/{playground}/{email}", to, playground, email);

	}
	
	
}
