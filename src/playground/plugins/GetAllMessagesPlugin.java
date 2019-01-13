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
public class GetAllMessagesPlugin implements PlaygroundPlugin {

	private ActivityDao activities;
	private ObjectMapper jackson;
	private int size;
	private int page;

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Autowired
	public void setActivities(ActivityDao activities) {
		this.activities = activities;
	}

	@Override
	public Object invokeOperation( ElementEntity element, UserEntity user, ActivityEntity activity) throws Exception {

		MessagePageable pages;

		pages = this.jackson.readValue(activity.getJsonAttributes(), MessagePageable.class);
		String pageStr = pages.getPage();
		String sizeStr = pages.getSize();
		this.page = Integer.parseInt(pageStr);
		this.size = Integer.parseInt(sizeStr);

		PostMessageResponse post;
		List<String> allPosts = new ArrayList<>();

		List<ActivityEntity> actList = this.activities
				.findAllByType("PostMessage", PageRequest.of(page, size, Direction.DESC, "elementId")).getContent();

		
		for (ActivityEntity activityEntity : actList) {
			System.err.println(activityEntity.getJsonAttributes());
			post = this.jackson.readValue(activityEntity.getJsonAttributes(), PostMessageResponse.class);
			allPosts.add(post.getMessage());
		}
		return new AllMessages(allPosts);

	}



}
