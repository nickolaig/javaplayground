package playground.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.ActivityService;
import playground.logic.ActivityTO;
import playground.logic.exceptions.ErrMsg;
import playground.logic.exceptions.InvalidInputException;
import playground.logic.exceptions.ValidCodeIncorrectExeption;

@RestController
public class webActivityController {
	
	//private ActivityTO activity;
	private ActivityService actService;
	
/*	public ActivityTO getActivity() {
		return activity;
	}*/
	
	@Autowired
	public void setActivity(ActivityService actSrv) {
		this.actService = actSrv;
	}
	
	
	/********************************************************************************************
	 *	POST /playground/activity/{userPlayground}/{email} input - ActivityTO output - Object	*
	 *	use case - use element 																	*
	 ********************************************************************************************/
	@RequestMapping(
					method = RequestMethod.POST,
					path = "/playground/activity/{userPlayground}/{email}",
					produces = MediaType.APPLICATION_JSON_VALUE,
					consumes = MediaType.APPLICATION_JSON_VALUE)
	public ActivityTO invokeActivity(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestBody ActivityTO activity) throws Exception {
		
				validateEmailNull(email);
				validatePlayground(userPlayground);
				System.err.println("ASDASDASDASD");
				return new ActivityTO(this.actService.createActivity(userPlayground, email, activity));
	}
	
		
		
/*		if(activity.getType().equals("showAllMessages")) {	
			List<Message> messages = Arrays.asList(
					new Message("Hello"),
					new Message("Welcome"),
					new Message("Hey")
			);*/
			
/*			return messages.toArray(new Message[0]);
		}*/
			
/*		return new Object();
	}*/
	
	private void validatePlayground(String userPlayground) throws Exception {
		if ("null".equals(userPlayground) || userPlayground == null || userPlayground.equals("")) {
			throw new InvalidInputException("playground cant be null or empty");
		}
	}

	private void validateEmailNull(String email) throws Exception {
		if ("null".equals(email) || email == null || email.equals("")) {
			throw new InvalidInputException("Email cant be null or empty");
		}
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrMsg handleValidCodeException (ValidCodeIncorrectExeption e) {
		String message = e.getMessage();
		if (message == null) {
			message = "There is no relevant message";
		}
		return new ErrMsg(message);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrMsg handleMissInputException (InvalidInputException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "There is no relevant message";
		}
		return new ErrMsg(message);
	}
}
