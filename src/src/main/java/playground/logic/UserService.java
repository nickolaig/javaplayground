package src.main.java.playground.logic;

public interface UserService {
	public UserEntity addNewUser(UserEntity user) throws Exception;
	public UserEntity getUserByEmail(String email) throws Exception;
	public void cleanup();
	public void updateUser(String email, UserEntity entity) throws Exception;
}
