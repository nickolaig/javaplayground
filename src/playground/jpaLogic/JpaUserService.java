package playground.jpaLogic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.MyLog;
import playground.dal.IdGeneratorDao;
import playground.dal.UserDao;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.exceptions.UserAlreadyExistsException;
import playground.logic.exceptions.UserNotFoundException;

@Service("UserService")
public class JpaUserService implements UserService {
	private UserDao users;
	private IdGeneratorDao idGenerator;

	@Autowired
	public JpaUserService(UserDao users, IdGeneratorDao idGenerator) {
		this.users = users;
		this.idGenerator = idGenerator;
	}

	@Override
	@Transactional
	@MyLog
	public UserEntity addNewUser(UserEntity user) throws Exception {
		UserKey userEmailPlaygroundKey = new UserKey(user.getUserEmailPlaygroundKey().getEmail(),
				user.getUserEmailPlaygroundKey().getPlayground());
		if (this.users.existsById(userEmailPlaygroundKey)) {
			throw new UserAlreadyExistsException("User exists with email: " + userEmailPlaygroundKey.getEmail()
					+ " in playground: " + userEmailPlaygroundKey.getPlayground());
		}

		if (!userEmailPlaygroundKey.getEmail().endsWith("ac.il")) {
			throw new Exception("email is incorrect");
		}

//		IdGenerator tmp = this.idGenerator.save(new IdGenerator());
//		Long dummyId = tmp.getId();
//		this.idGenerator.delete(tmp);

		return this.users.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	public UserEntity getUserByEmailAndPlayground(UserKey userKey) throws Exception {

		return this.users.findById(userKey)
				.orElseThrow(() -> new UserNotFoundException("no user with email: " + userKey.getEmail()));
	}

	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.users.deleteAll();
		;
	}

	@Override
	@Transactional
	@MyLog
	public void updateUser(UserKey userEmailPlaygroundKey, UserEntity entityUpdates) throws Exception {

		// if isnt existing throw exception
		UserEntity existing = this.users.findById(userEmailPlaygroundKey).orElseThrow(
				() -> new UserNotFoundException("No such user with email: " + userEmailPlaygroundKey.getEmail()
						+ " and playground: " + userEmailPlaygroundKey.getPlayground()));

		if (entityUpdates.getUserName() != null & entityUpdates.getUserName() != existing.getUserName()) {
			existing.setUserName(entityUpdates.getUserName());
		}

		if (entityUpdates.getAvatar() != null & entityUpdates.getAvatar() != existing.getAvatar()) {
			existing.setAvatar(entityUpdates.getAvatar());
		}

		if (entityUpdates.getRole() != null & entityUpdates.getRole() != existing.getRole()) {
			existing.setRole(entityUpdates.getRole());
		}

		if (entityUpdates.getPoints() != null & entityUpdates.getPoints() != existing.getPoints()) {
			existing.setPoints(entityUpdates.getPoints());
		}

		if (entityUpdates.getCode() != null & entityUpdates.getCode() != existing.getCode()) {
			existing.setCode(entityUpdates.getCode());
		}

		if (entityUpdates.getCode() != null & entityUpdates.getCode() != existing.getCode()) {
			existing.setCode(entityUpdates.getCode());
		}

		if (entityUpdates.getIsValidate() != null & entityUpdates.getIsValidate() != existing.getIsValidate()) {
			existing.setIsValidate(entityUpdates.getIsValidate());
		}

		this.users.save(existing);
	}

}
