package playground.jpaLogic;

import java.util.Optional;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.aop.MyLog;
import playground.aop.ValidationManagerLog;
import playground.dal.ActivityDao;
import playground.dal.IdGeneratorDao;
import playground.logic.ActivityEntity;
import playground.logic.ActivityService;
import playground.logic.ActivityTO;
import playground.logic.exceptions.ActivityAlreadyExistException;
import playground.logic.exceptions.ActivityNotFoundException;
import playground.logic.exceptions.InvalidInputException;
import playground.plugins.PlaygroundPlugin;


@Service
public class JpaActivityService implements ActivityService {

	private ActivityDao activities;
	private ApplicationContext spring;
	private ObjectMapper jackson;
	private IdGeneratorDao idGenerator;
	

	@Autowired
	public JpaActivityService(ActivityDao activities, IdGeneratorDao idGenerator, ApplicationContext spring) {
		this.activities = activities;
		this.idGenerator = idGenerator;
		this.spring = spring; //???
		this.jackson = new ObjectMapper();
	}
	
	@Override
	@Transactional(readOnly = true)
	public ActivityEntity getActivity(String id) throws ActivityNotFoundException {
		Optional<ActivityEntity> op = this.activities.findById(id);
		if(op.isPresent())
			return op.get();
		throw new ActivityNotFoundException();
	}
	
	
	@Transactional
	@MyLog
	@Override
	public void updateActivity(String id, ActivityEntity activityEntity) throws ActivityNotFoundException  {
		ActivityEntity existingActivity = this.getActivity(id);
		
		if(activityEntity.getType()!= null && !activityEntity.getType().equals(existingActivity.getType())) {
			existingActivity.setType(activityEntity.getType());
		}
		
		if(activityEntity.getElementPlayground()!= null && activityEntity.getElementPlayground()!= existingActivity.getElementPlayground()) {
			existingActivity.setElementPlayground(activityEntity.getElementPlayground());
		}
		
		this.activities.save(existingActivity);
	}
	
	
	@Override
	@Transactional
	@MyLog
	public List<ActivityEntity> getAllActivities (int size,int page){
		return this.activities
				.findAll(PageRequest.of(page, size))
				.getContent();
	}
	
	@Override
	@Transactional
	public ActivityEntity createActivity(String email, String userPlayground, ActivityEntity activity) throws ActivityAlreadyExistException{
		if (!this.activities.existsById(activity.getId())) {
			IdGenerator tmp = this.idGenerator.save(new IdGenerator());
			Long dummyId = tmp.getId();
			this.idGenerator.delete(tmp);
			activity.setId("" + dummyId);
		}
		
		activity.setPlayerEmail(email);
		activity.setPlayerPlayground(userPlayground);
		try {
			if(activity.getType()!=null) {
				String type = activity.getType();
				String className = "playground.plugins." + type + "Plugin";
				Class<?> theClass = Class.forName(className);
				System.err.println(theClass.getName());
				PlaygroundPlugin plugin = (PlaygroundPlugin) this.spring.getBean(theClass);
				
				//PlaygroundPlugin plugin = (PlaygroundPlugin) spring.getBean(Class.forName("playground.plugins." + type + "Plugin"));
				
				Object content = plugin.invokeOperation(activity);
				Map<String,Object> contentMap = this.jackson.readValue(this.jackson.writeValueAsString(content), Map.class);
				System.err.println(contentMap);
				activity.getAttributes().putAll(contentMap);
				System.err.println(activity);
				//activity = jackson.readValue(jackson.writeValueAsString(content),ActivityEntity.class);
		
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		System.err.println("before SAVEEEEEEEEEEEEEEEEEEEEEEEe   " + activity);
		return this.activities.save(activity);
	}
	
	public List<ActivityEntity> getAllPostMessagesByElementId(String elementId, String type, Pageable pageable){
		return activities.getAllPostMessagesByElementId(elementId, type, pageable);
	}
	
	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.activities.deleteAll();
	}


}
