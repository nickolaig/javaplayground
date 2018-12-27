package playground.logic;

import java.util.List;

import playground.logic.exceptions.ActivityAlreadyExistException;
import playground.logic.exceptions.ActivityNotFoundException;
import playground.logic.exceptions.InvalidInputException;

public interface ActivityService {

	public ActivityTO invokeActivity(ActivityTO activity) throws InvalidInputException;
	public ActivityEntity getActivity(String id) throws ActivityNotFoundException;
	public List<ActivityEntity> getAllActivities(int size, int page);
	void cleanup();
	public ActivityEntity createActivity(ActivityEntity activity) throws ActivityAlreadyExistException;
	
	
}
