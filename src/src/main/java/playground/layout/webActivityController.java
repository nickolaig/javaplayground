package src.main.java.playground.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import src.main.java.playground.logic.ActivityTO;
import src.main.java.playground.logic.ElementTO;

public class webActivityController {
	
	private ActivityTO activity;

	public ActivityTO getActivity() {
		return activity;
	}
	
	@Autowired
	public void setActivity(ActivityTO activity) {
		this.activity = activity;
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/activities/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public Object useElement(@RequestBody ActivityTO activity, 
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) {
		
		return new Object();
	}
}
