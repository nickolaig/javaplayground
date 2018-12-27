package playground.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
//import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.exceptions.UserNotAuthorizedException;
import playground.logic.exceptions.UserNotConfirmedException;
import playground.logic.exceptions.UserNotFoundException;

@Component
@Aspect
public class LoggerAspect {
	private Log log = LogFactory.getLog(LoggerAspect.class);
	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Around("@annotation(playground.aop.MyLog)")
	public Object log (ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String methodSignature = className + "." + methodName + "()";
//		System.err.println(methodSignature + " - start");
		log.info(methodSignature + " - start ******************");
		
		try {
			Object rv = joinPoint.proceed();
			log.info(methodSignature + " - ended successfully **********************");
			return rv;
		} catch (Throwable e) {
			log.error(methodSignature + " - end with error " + e.getClass().getName());
			throw e;
		}
	}
	
	@Around("@annotation(playground.aop.MyLog) && args(userPlayground, email,..)")
	public Object log (ProceedingJoinPoint joinPoint, String userPlayground, String email) throws Throwable {
		log.info("userPlayground: " + userPlayground + ", email: " + email);
		
		try {
			//check if user exists
			UserEntity user = userService.getUserByEmailAndPlayground(new UserKey(email, userPlayground));
			
			//check for confirmation
			if(!user.getIsValidate()) {
				throw new UserNotConfirmedException("User not confirmed in the system yet...");
			}
			
			//check if hes authorized
			if(!user.getRole().equalsIgnoreCase("manager")) {
				throw new UserNotAuthorizedException("User does not authorized for that action");
			}
			
		} catch(RuntimeException e) {
			throw new RuntimeException(e);
		}
		
		return joinPoint.proceed();
	}
	
	@Around("@annotation(playground.aop.ValidationManagerLog) && args(userPlayground, email,..)")
	public Object logValidateManagerAction (ProceedingJoinPoint joinPoint, String userPlayground, String email) throws Throwable {
		log.info("userPlayground: " + userPlayground + ", email: " + email);
		
		
		try {
			UserEntity user = userService.getUserByEmailAndPlayground(new UserKey(email, userPlayground));
			
			//check if hes authorized
			if(!user.getRole().equalsIgnoreCase("manager")) {
				throw new UserNotAuthorizedException("User does not authorized for that action");
			}
			
		} catch(RuntimeException e) {
			throw new RuntimeException(e);
		}
		
		return joinPoint.proceed();
	}
	
//	@Around("@annotation(playground.aop.ValidationPlayerLog) && args(userPlayground, email,..)")
//	public Object logValidatePlayerAction (ProceedingJoinPoint joinPoint, String userPlayground, String email) throws Throwable {
//		log.info("userPlayground: " + userPlayground + ", email: " + email);
//		
//		
//		try {
//			UserEntity user = userService.getUserByEmailAndPlayground(new UserKey(email, userPlayground));
//			
//			//check if hes authorized
//			if(!user.getRole().equalsIgnoreCase("player")) {
//				throw new UserNotAuthorizedException("User does not authorized for that action");
//			}
//			
//		} catch(RuntimeException e) {
//			throw new RuntimeException(e);
//		}
//		
//		return joinPoint.proceed();
//	}
}
