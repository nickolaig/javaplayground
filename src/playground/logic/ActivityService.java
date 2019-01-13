package playground.logic;

import java.util.List;

import org.springframework.data.domain.Pageable;

import playground.logic.exceptions.ActivityAlreadyExistException;
import playground.logic.exceptions.ActivityNotFoundException;
import playground.logic.exceptions.InvalidInputException;

public interface ActivityService {

	public void cleanup();
	public ActivityEntity createActivity(String email, String userPlayground, ActivityTO activity) throws Exception;
	
}
