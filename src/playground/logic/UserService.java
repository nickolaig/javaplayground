package playground.logic;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
	public UserEntity addNewUser(UserEntity user) throws Exception;
	public UserEntity getUserByEmailAndPlayground(String email, String playground) throws Exception;
	public void cleanup();
	public void updateUser(UserKey userEmailPlaygroundKey, UserEntity entityUpdates) throws Exception;
}
