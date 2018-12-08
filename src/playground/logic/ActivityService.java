package playground.logic;

import playground.logic.exceptions.InvalidInputException;

public interface ActivityService {

	public ActivityTO invokeActivity(ActivityTO activity) throws InvalidInputException;
}
