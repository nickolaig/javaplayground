package playground.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;

@Component
@Aspect
public class CheckValidActionByRuleAspect {
	
	private UserService userService;
	private Log log = LogFactory.getLog(CheckValidActionByRuleAspect.class);
	private String role;
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Before("@annotation(playground.aop.CheckValidActionByRule) && args (playground,email,..)")
	public void checkUserWithArgsAnalysis(JoinPoint jp, String playground, String email) throws Throwable {

		MethodSignature signature = (MethodSignature) jp.getSignature();

		Method method = signature.getMethod();
		
		CheckValidActionByRule annotation = method.getAnnotation(CheckValidActionByRule.class);

		this.role = annotation.role();
		
		UserEntity user = this.userService.getUserByEmailAndPlayground(new UserKey(email, playground));

		if (user.getRole().equals("Manager") && this.role.equals("Manager")) {
			this.log.info("*********************** user is manager");
			throw new RuntimeException("Only PLAYER can do in action");

		}

		if (user.getRole().equals("Player") && this.role.equals("Player")) {
			this.log.info("*********************** user is play");
			throw new RuntimeException("Only MANAGER can do this the Action");

		}

	}
}
