package playground.jpaLogic;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.MyLog;
import playground.aop.checkForUserConfirmation;
import playground.dal.IdGeneratorDao;
import playground.dal.UserDao;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.exceptions.InvalidInputException;
import playground.logic.exceptions.UserAlreadyExistsException;
import playground.logic.exceptions.UserNotFoundException;
import playground.logic.exceptions.ValidCodeIncorrectExeption;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.*;

@Service("UserService")
public class JpaUserService implements UserService {
	private UserDao users;
	private IdGeneratorDao idGenerator;
	private Properties prop ;
	
	@Autowired
	public JpaUserService(UserDao users, IdGeneratorDao idGenerator) {
		this.users = users;
		this.idGenerator = idGenerator;
	}
	@PostConstruct
	public void init() {
		this.prop = new Properties();
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
	}
	@Override
	@Transactional
	@MyLog
	public UserEntity addNewUser(UserEntity user) throws Exception {
		
		String host = "stmp.gmail.com";
		String username = "nickolaig@mail.afeka.ac.il";
		String from = "nickolaig@mail.afeka.ac.il";
		String pass = "afeka2016";
		String to = user.getUserEmailPlaygroundKey().getEmail();
		String subject = "Confirm password From TA playground";
		
		
		UserKey userEmailPlaygroundKey = new UserKey(user.getUserEmailPlaygroundKey().getEmail(),
				user.getUserEmailPlaygroundKey().getPlayground());
		
		if (this.users.existsById(userEmailPlaygroundKey)) {
			throw new UserAlreadyExistsException("User exists with email: " + userEmailPlaygroundKey.getEmail()
					+ " in playground: " + userEmailPlaygroundKey.getPlayground());
		}
		
		user.setCode(generateCode());
		user.setIsValidate(false);

		UserEntity rv = this.users.save(user);
		
		
		
		Session session = Session.getDefaultInstance(prop,new Authenticator() {	
		@Override
		protected PasswordAuthentication	getPasswordAuthentication() {
		
			return new PasswordAuthentication(username, pass);
		}
		});
	
		try {
			
			Message message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			
			message.setSubject(subject);
		
			message.setText("Your confirmation code is : " +  user.getCode() + " Use it to come back!");
		
			Transport.send(message);
		
			System.err.println("Email send successfully...");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return rv;
	}

	
	
	
	@Override
	@Transactional
	@MyLog
	public UserEntity confirm(String playground, String email, int code) throws Exception {

		UserEntity rv = this.getUserByEmailAndPlayground(new UserKey(email, playground));

		if (rv != null) {
			if (rv.getCode()== code) {
				rv.setIsValidate(true);

				return this.users.save(rv);
			} else {
				throw new ValidCodeIncorrectExeption("The valid code is not correct!");
			}
		} else {
			throw new InvalidInputException("no user found for email : " + email);
		}
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
	@checkForUserConfirmation
	public void updateUser(UserEntity entityUpdates, UserKey userEmailPlaygroundKey ) throws Exception {
		boolean isSelfUpdate;
		
		// if isnt existing throw exception
		UserEntity existing = this.getUserByEmailAndPlayground(userEmailPlaygroundKey);
		UserEntity existingOldUser=null;
		
		//check if its update for the same user
		if(existing.getUserEmailPlaygroundKey().getEmail().equals(entityUpdates.getUserEmailPlaygroundKey().getEmail()) && 
		 existing.getUserEmailPlaygroundKey().getPlayground().equals(entityUpdates.getUserEmailPlaygroundKey().getPlayground())){
			isSelfUpdate=true;
		}
		else {
			isSelfUpdate=false;
			existingOldUser=this.getUserByEmailAndPlayground(entityUpdates.getUserEmailPlaygroundKey());
		}
		
		switch (existing.getRole()) {
		case "Manager":
			if(isSelfUpdate) {
				if (entityUpdates.getUserName() != null && !entityUpdates.getUserName() .equals( existing.getUserName())) {
					existing.setUserName(entityUpdates.getUserName());
				}

				if (entityUpdates.getAvatar() != null && !entityUpdates.getAvatar() .equals( existing.getAvatar())) {
					existing.setAvatar(entityUpdates.getAvatar());
				}

				if (entityUpdates.getRole() != null && entityUpdates.getRole() != existing.getRole()) {
					existing.setRole(entityUpdates.getRole());
					existing.setPoints(0L);
				}
				this.users.save(existing);

			} else {
				if (entityUpdates.getUserName() != null && !entityUpdates.getUserName().equals(existingOldUser.getUserName())) {
					throw new Exception("Manager can't update player username");
				}

				if (entityUpdates.getAvatar() != null && !entityUpdates.getAvatar().equals(existingOldUser.getAvatar())) {
					throw new Exception("Manager can't update player avatar");
				}

				if (entityUpdates.getRole() != null && !entityUpdates.getRole().equals(existingOldUser.getRole())) {
					throw new Exception("Manager can't update player role");
				}
				
				if (entityUpdates.getPoints() != null && entityUpdates.getPoints() != existingOldUser.getPoints()) {
					existingOldUser.setPoints(entityUpdates.getPoints());
				}
				if (entityUpdates.isEnabled() != null && entityUpdates.isEnabled() != existingOldUser.isEnabled()) {
					existingOldUser.setEnabled(!existingOldUser.isEnabled());
				}
				this.users.save(existingOldUser);
			}
			break;
		
		case "Player":
			if(isSelfUpdate) {
				if (entityUpdates.getUserName() != null &! entityUpdates.getUserName().equals( existing.getUserName())) {
					existing.setUserName(entityUpdates.getUserName());
				}

				if (entityUpdates.getAvatar() != null & !entityUpdates.getAvatar() .equals( existing.getAvatar())) {
					existing.setAvatar(entityUpdates.getAvatar());
				}

				if (entityUpdates.getRole() != null & !entityUpdates.getRole() .equals(existing.getRole())) {
					existing.setRole(entityUpdates.getRole());
					existing.setPoints(0L);
				}
				this.users.save(existing);
			} else {
				throw new Exception("Player Cant Update Another User!");
			}
			break;
		}

	}
	
	private int generateCode() {
		int minRange = 5;
		int maxRange = 9;
		int generatedCode = 0;

		int randomNumLength = ThreadLocalRandom.current().nextInt(minRange, maxRange + 1);
		int randomDigit;
		
		for(int i = 0 ; i < randomNumLength ; i++) {
			randomDigit = ThreadLocalRandom.current().nextInt(minRange, maxRange + 1);
			generatedCode += Math.pow(10, i);
			generatedCode += randomDigit; 
		}
		
		return generatedCode;
	}
}
