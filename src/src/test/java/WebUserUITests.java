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
import com.fasterxml.jackson.databind.ObjectMapper;
import src.main.java.playground.Application;
import src.main.java.playground.layout.UserTO;
import src.main.java.playground.logic.NewUserForm;
import src.main.java.playground.logic.UserEntity;
import src.main.java.playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class WebUserUITests {
	@LocalServerPort
	private int port;

	private String url;

	private RestTemplate restTemplate;
	@Autowired
	private UserService userService;

	private ObjectMapper jsonMapper;

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

		String email = "kroyzman546@ac.il";
		NewUserForm userForm = new NewUserForm(email, "Nadav", "Player", "loYodea");

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
		assertThat(actualReturnedUser.getEmail()).isEqualTo(email);

		UserEntity expectedOutcome = new UserEntity(email, "Nadi", "Nadav", "loYodea", "Player", 0L);
		assertThat(this.userService.getUserByEmail(email)).isNotNull()
				.extracting("email", "playground", "userName", "avatar", "role", "points").containsExactly(
						expectedOutcome.getEmail(), expectedOutcome.getPlayground(), expectedOutcome.getUserName(),
						expectedOutcome.getAvatar(), expectedOutcome.getRole(), expectedOutcome.getPoints());

	}

	@Test(expected = Exception.class)
	public void testCreateUserWithExistingUser() throws Exception {
		String email = "kroyzman546@ac.il";
		NewUserForm userForm = new NewUserForm(email, "Nadav", "Player", "loYodea");
		UserEntity existingUser = new UserEntity(email, "Nadi", "benny", "mashu", "Manager", 0L);

		// Given the server is up And the database contains a user with email
		// "kroyzman546@gmail.com"
		userService.addNewUser(existingUser);

		// When
		UserTO returnedUser = this.restTemplate.postForObject(this.url, userForm, UserTO.class);
	}

	@Test(expected = Exception.class)
	public void testCreateUserWithInvalidEmail() {
		String email = "benny@gmail.com";
		NewUserForm userForm = new NewUserForm(email, "Nadav", "Player", "loYodea");

		UserTO actualReturnedUser = this.restTemplate.postForObject(this.url, userForm, UserTO.class);
	}

	@Test
	public void testUserLoginSuccessfully() throws Exception {
		String email = "benny@ac.il";
		String playground = "TA";

		// Given database contains {email : "benny@ac.il" , playground = "TA"}
		this.userService.addNewUser(new UserEntity(email, playground, "Idan", "Avatar", "Player", 1L));

		// When I invoke GET this.url + "/login/TA/benny@ac.il"
		UserTO actualUser = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class,
				playground, email);

		assertThat(actualUser).isNotNull().extracting("email", "playground").containsExactly(email, playground);
	}

	@Test(expected = Exception.class)
	public void testUserLoginWithNonExistingUser() {
		String email = "benny@ac.il";
		String playground = "TA";

		UserTO actualUser = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class,
				playground, email);
	}

	@Test(expected = Exception.class)
	public void testUserLoginWithIncorrectEmail() {
		String email = "benny@gmail.com";
		String playground = "TA";

		// When I invoke GET this.url + "/login/TA/benny@ac.il"
		UserTO actualUser = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class,
				playground, email);
	}

	@Test
	public void testUserConfirmationCorrectly() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		String code = "1234";

		// Given database contains {email : "benny@ac.il" , playground = "TA"}
		this.userService.addNewUser(new UserEntity(email, playground, "Maayan", "Avatar", "Manager", 0L));

		// When I invoke GET this.url + "/confirm/TA/benny@ac.il/1234"
		UserTO actualUser = this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}",
				UserTO.class, playground, email, code);

		assertThat(actualUser).isNotNull().extracting("email", "playground", "userName", "avatar", "role", "points")
				.containsExactly(email, playground, "Maayan", "Avatar", "Manager", 0L);
	}

	@Test(expected = Exception.class)
	public void testUserConfirmationWithIncorrectCode() throws Exception {
		String playground = "TA";
		String email = "benny@ac.il";
		String code = "123";

		// Given database contains {email : "benny@ac.il" , playground = "TA"}
		this.userService.addNewUser(new UserEntity(email, playground, "Maayan", "Avatar", "Manager", 0L));

		// When I invoke GET this.url + "/confirm/TA/benny@ac.il/123"
		UserTO actualUser = this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}",
				UserTO.class, playground, email, code);
	}

	@Test
	public void testUpdateUserDetailsSuccessfully() throws Exception {
		String email = "benny@ac.il";

		// given
		String entityJson = "{\"email\":\"benny@ac.il\", \"playground\":\"TA\", \"userName\":\"benny\", \"avatar\":\"loyodea\", \"role\":\"Manager\", \"points\":0}";
		UserEntity oldUser = this.jsonMapper.readValue(entityJson, UserEntity.class);

		this.userService.addNewUser(oldUser);

		// when
		String userToString = "{\"email\":\"benny@ac.il\", \"playground\":\"TA\", \"userName\":\"benny4k\", \"avatar\":\"loyodea\", \"role\":\"Player\", \"points\":5}";
		UserTO to = this.jsonMapper.readValue(userToString, UserTO.class);

		this.restTemplate.put(this.url + "/{playground}/{email}", to, "TA", email);

		// then
		UserEntity actualEntity = this.userService.getUserByEmail(email);
		assertThat(actualEntity).isNotNull().extracting("userName", "role", "points").containsExactly("benny4k",
				"Player", 5L);
	}

	@Test(expected = Exception.class)
	public void testUpdateWithNonExistingUser() throws Exception {
		String email = "benny@ac.il";

		// when
		String userToString = "{\"email\":\"benny@ac.il\", \"playground\":\"TA\", \"userName\":\"benny4k\", \"avatar\":\"loyodea\", \"role\":\"Player\", \"points\":5}";
		UserTO to = this.jsonMapper.readValue(userToString, UserTO.class);

		this.restTemplate.put(this.url + "/{playground}/{email}", to, "TA", email);

	}
}
