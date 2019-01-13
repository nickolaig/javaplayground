package playground.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.NewUserForm;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.UserTO;
import playground.logic.exceptions.UserIncorrectEmail;

@RestController
public class WebUserController {

	private UserTO user;
	private NewUserForm userForm;
	private UserService userService;
	private static final String DEFAULT_PLAYGROUND = "TA";
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserTO getUser() {
		return user;
	}

	@Autowired
	public void setUser(UserTO user) {
		this.user = user;
	}

	public NewUserForm getUserForm() {
		return userForm;
	}

	@Autowired
	public void setUserForm(NewUserForm userForm) {
		this.userForm = userForm;
	}

	@RequestMapping(method = RequestMethod.POST, path = "/playground/users", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserTO registerNewUser(@RequestBody NewUserForm userForm) throws Exception {
		
		validateEmail(userForm.getEmail());
		UserTO returnedUser = new UserTO(userForm.getEmail(), DEFAULT_PLAYGROUND, userForm.getUserName(), userForm.getAvatar(),
				userForm.getRole());
		return new UserTO(userService.addNewUser(returnedUser.toEntity()));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/playground/users/confirm/{playground}/{email}/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO userValidation(@PathVariable("playground") String playground, @PathVariable("email") String email,
			@PathVariable("code") int code) throws Exception {
		
		UserEntity currentUser = this.userService.getUserByEmailAndPlayground(new UserKey(email, playground));
		
		if (code == currentUser.getCode()) {
			currentUser.setIsValidate(true);
			
			this.userService.updateUser(currentUser, currentUser.getUserEmailPlaygroundKey());
			
			return new UserTO(currentUser);
		} else {
			throw new Exception("Invalid code");
		}

	}

	@RequestMapping(method = RequestMethod.GET, path = "/playground/users/login/{playground}/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO userLogin(@PathVariable("playground") String playground, @PathVariable("email") String email)
			throws Exception {

		return new UserTO(this.userService.getUserByEmailAndPlayground(new UserKey(email, playground)));

	}

	@RequestMapping(method = RequestMethod.PUT, path = "/playground/users/{playground}/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUserDetails(@PathVariable("playground") String playground, @PathVariable("email") String email,
			@RequestBody UserTO newUser) throws Exception {
		this.userService.updateUser(newUser.toEntity(), new UserKey(email, playground));
	}
	
	private void validateEmail(String email) throws Exception {
		if ("null".equals(email) || email == null || email.equals("")) {
			throw new UserIncorrectEmail("Email cant be null or empty");
		}
		
	}

}
