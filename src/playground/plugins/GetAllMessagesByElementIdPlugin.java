package playground.plugins;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import playground.dal.ActivityDao;
import playground.logic.ActivityEntity;
import playground.logic.ElementEntity;
import playground.logic.UserEntity;

@Component
public class GetAllMessagesByElementIdPlugin implements PlaygroundPlugin {

	private ActivityDao activities;
	private ObjectMapper jackson;
	private int size;
	private int page;
	private String elementId;
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Autowired
	public void setActivities(ActivityDao activities) {
		this.activities = activities;
	}
	
	@Override
	public Object invokeOperation(ElementEntity element, UserEntity user, ActivityEntity activity) throws Exception {

		MessagePageableByElementId pagesWithElementId;

		pagesWithElementId = this.jackson.readValue(activity.getJsonAttributes(), MessagePageableByElementId.class);
		String pageStr = pagesWithElementId.getPage();
		String sizeStr = pagesWithElementId.getSize();
		this.page = Integer.parseInt(pageStr);
		this.size = Integer.parseInt(sizeStr);
		this.elementId=pagesWithElementId.getElementId();
		System.err.println(elementId+"---------------------------dasdasdas");
		PostMessageResponse post;
		List<String> allPosts = new ArrayList<>();
		if(elementId==null)
			throw new Exception("Element id can not be null");
		List<ActivityEntity> actList = this.activities
				.findAllByTypeAndElementId("PostMessage",this.elementId, PageRequest.of(page, size)).getContent();

		
		for (ActivityEntity activityEntity : actList) {
			System.err.println(activityEntity.getJsonAttributes());
			post = this.jackson.readValue(activityEntity.getJsonAttributes(), PostMessageResponse.class);
			allPosts.add(post.getMessage());
		}
		return new AllMessages(allPosts);

	}



}
