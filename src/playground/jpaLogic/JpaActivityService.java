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

import playground.aop.CheckValidActionByRule;
import playground.aop.MyLog;
import playground.aop.ValidationManagerLog;
import playground.aop.checkForUserConfirmation;
import playground.dal.ActivityDao;
import playground.dal.IdGeneratorDao;
import playground.logic.ActivityEntity;
import playground.logic.ActivityKey;
import playground.logic.ActivityService;
import playground.logic.ActivityTO;
import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
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
	
	private ElementService elements;
	private UserService users;
	
	@Autowired
	public JpaActivityService(ActivityDao activities, IdGeneratorDao idGenerator, ApplicationContext spring,ElementService elements,UserService users) {
		this.activities = activities;
		this.idGenerator = idGenerator;
		this.spring = spring; //???
		this.jackson = new ObjectMapper();
		
		this.elements= elements;
		this.users=users;
	}
	

	@Override
	@Transactional
	@MyLog
	@checkForUserConfirmation
	@CheckValidActionByRule(role = "Manager")
	public ActivityEntity createActivity(String userPlayground, String email, ActivityTO activity) throws Exception{

			IdGenerator tmp = this.idGenerator.save(new IdGenerator());
			Long dummyId = tmp.getId();
			this.idGenerator.delete(tmp);
			activity.setId(dummyId.toString());
			activity.setPlayground(userPlayground);
		
			ActivityEntity rv = null;
		
			UserEntity user = this.users.getUserByEmailAndPlayground(new UserKey(email,userPlayground));
			System.err.println(user.getUserEmailPlaygroundKey().getEmail() + "-----" + user.getUserEmailPlaygroundKey().getPlayground());
			// check if the element exist in the playground
			ElementEntity element = this.elements.getElementById(userPlayground, email, activity.getElementPlayground(), activity.getElementId());


			ActivityEntity activityEntity = activity.toEntity();


		
		try {
			if(activityEntity.getType()!=null) {
				String type = activityEntity.getType();
				activityEntity.setPlayerEmail(email);
				activityEntity.setPlayerPlayground(userPlayground);
				String className = "playground.plugins." + type + "Plugin";
				Class<?> theClass = Class.forName(className);
				System.err.println(theClass.getName());
				PlaygroundPlugin plugin = (PlaygroundPlugin) this.spring.getBean(theClass);
				
				//PlaygroundPlugin plugin = (PlaygroundPlugin) spring.getBean(Class.forName("playground.plugins." + type + "Plugin"));
				
				Object content = plugin.invokeOperation(element,user, activityEntity);
				Map<String,Object> contentMap = this.jackson.readValue(this.jackson.writeValueAsString(content), Map.class);
				System.err.println(contentMap);
				activityEntity.getAttributes().putAll(contentMap);
				System.err.println(activityEntity);
				//activity = jackson.readValue(jackson.writeValueAsString(content),ActivityEntity.class);
				rv=this.activities.save(activityEntity);
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}

		return rv;
	}

	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.activities.deleteAll();
	}


}
