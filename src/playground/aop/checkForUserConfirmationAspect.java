package playground.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;

@Component
@Aspect
public class checkForUserConfirmationAspect {
	
	private UserService userService;
	private Log log = LogFactory.getLog(checkForUserConfirmationAspect.class);
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Before("@annotation(playground.aop.checkForUserConfirmation) && args (playground,email,..)")
	public void checkConfirmWithArgsAnalysis(JoinPoint jp, String playground, String email) throws Throwable {
		
		UserEntity user = this.userService.getUserByEmailAndPlayground(new UserKey(email, playground));
		
		if(!user.getIsValidate()) {
			this.log.info("*********************** user is NOT confirmed");
			throw new RuntimeException("Please confirm your user before you do any action in the system!");
		}
		this.log.info("*********************** user is confirmed");	
		
	}
}
