package playground.logic;

import java.util.List;

import org.springframework.data.domain.Pageable;

import playground.logic.exceptions.ActivityAlreadyExistException;
import playground.logic.exceptions.ActivityNotFoundException;
import playground.logic.exceptions.InvalidInputException;

public interface ActivityService {

	public ActivityEntity getActivity(String id) throws ActivityNotFoundException;
	public List<ActivityEntity> getAllActivities(int size, int page);
	void cleanup();
	public ActivityEntity createActivity(String email, String userPlayground, ActivityEntity activity) throws ActivityAlreadyExistException;
	void updateActivity(String id, ActivityEntity activityEntity) throws ActivityNotFoundException;
	public List<ActivityEntity> getAllPostMessagesByElementId(String elementId, String type, Pageable pageable);
	
}
