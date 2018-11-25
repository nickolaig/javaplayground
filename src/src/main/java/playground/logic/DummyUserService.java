package src.main.java.playground.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;



@Service
public class DummyUserService implements UserService {
	private Map<String, UserEntity> users;

	@PostConstruct
	public void init() {
		this.users = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public UserEntity addNewUser(UserEntity user) throws Exception {
		if (this.users.containsKey(user.getEmail())) {
			throw new UserAlreadyExistsException("User exists with email: " + user.getEmail());
		}
		
		if(!user.getEmail().endsWith("ac.il")) {
			throw new Exception("email is incorrect");
		}
		this.users.put(user.getEmail(), user);
		return user;
	}

	@Override
	public UserEntity getUserByEmail(String email) throws Exception {
		UserEntity rv = this.users.get(email);
		
		if(rv == null) {
			throw new UserNotFoundException("no user with email: " + email);
		}
		return rv;
	}

	@Override
	public void cleanup() {
		users.clear();
	}

	@Override
	public void updateUser(String email, UserEntity entityUpdates) throws Exception {
		UserEntity existing = this.users.get(email);
		if (existing == null) {
			throw new UserNotFoundException("no user with email: " + email);
		}
		boolean dirty = false;
		if (entityUpdates.getPlayground() != null & entityUpdates.getPlayground() != existing.getPlayground()) {
			existing.setPlayground(entityUpdates.getPlayground());
			dirty = true;
		}
		if (entityUpdates.getUserName() != null & entityUpdates.getUserName() != existing.getUserName()) {
			existing.setUserName(entityUpdates.getUserName());
			dirty = true;
		}
		if (entityUpdates.getAvatar() != null & entityUpdates.getAvatar() != existing.getAvatar()) {
			existing.setAvatar(entityUpdates.getAvatar());
			dirty = true;
		}
		if (entityUpdates.getRole() != null & entityUpdates.getRole() != existing.getRole()) {
			existing.setRole(entityUpdates.getRole());
			dirty = true;
		}
		if (entityUpdates.getPoints() != null & entityUpdates.getPoints() != existing.getPoints()) {
			existing.setPoints(entityUpdates.getPoints());
			dirty = true;
		}
		if (dirty) {
			this.users.put(existing.getEmail(), existing);
		}
	}

}
