package src.main.java.playground.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import src.main.java.playground.logic.Message;
import src.main.java.playground.logic.NewUserForm;
import src.main.java.playground.logic.UserTO;

@RestController
public class WebUserController {
	
	private UserTO user;
	private NewUserForm userForm;
	
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
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/users",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public UserTO registerNewUser(@RequestBody NewUserForm userForm) {
		return new UserTO();
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/confirm/{playground}/{email}/{code}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO userValidation(@PathVariable("playground") String playground,
			@PathVariable("email") String email, @PathVariable("code") String code) throws Exception {
		
		//if code length is even --> the code is ok
		if(code.length() % 2 == 0) {
			return new UserTO(email, playground, "Maayan", "Avatar", "Manager", 0);
		} else {
			throw new Exception("Invalid code");
		}
		
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/login/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO userLogin(@PathVariable("playground") String playground, @PathVariable("email") String email) throws Exception {
		
		if(email.endsWith("ac.il")) {
			return new UserTO(email, playground, "Idan", "Avatar", "Player", 1);
		} else {
			throw new Exception("email is incorrect");
		}
		
	}
	
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/users/{playground}/{email}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateUserDetails(@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@RequestBody UserTO user) {
		//TODO
	}
	
	
}
