package src.main.java.playground.logic;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DummyUserService implements UserService{
	private Map<String, UserEntity> useres;
	
	@PostConstruct
	public void init() {
		this.useres = new HashMap<>();
	}

	@Override
	public UserEntity addNewUser(UserEntity user) {
		this.useres.put(user.getUserName(), user);
		return user;
	}
	
}
