package src.main.java.playground.logic;

public interface UserService {
	public UserEntity addNewUser(UserEntity user);
	public UserEntity getUserByEmail(String email);
	public void cleanup();
}
