package playground.logic;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
	public UserEntity addNewUser(UserEntity user) throws Exception;
	public UserEntity getUserByEmailAndPlayground(UserKey userKey) throws Exception;
	public void cleanup();
	public void updateUser(UserEntity entityUpdates, UserKey userEmailPlaygroundKey) throws Exception;
}
