package playground.jpaLogic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import playground.logic.ActivityRepository;
import playground.logic.ActivityService;
import playground.logic.ActivityTO;
import playground.logic.exceptions.InvalidInputException;

@Service
public class JpaActivityService implements ActivityService {

	@Autowired
	private ActivityRepository actRepository;
	
	@Override
	public ActivityTO invokeActivity(ActivityTO activity) throws InvalidInputException {
		
		if(activity.getType().equals("pMessage")) {
			
			return activity;
		}
		else
			throw new InvalidInputException("activity does not found");
	}

}
