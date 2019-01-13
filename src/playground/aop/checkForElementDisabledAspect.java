package playground.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;

public class checkForElementDisabledAspect {
	private ElementService elementService;
	private Log log = LogFactory.getLog(checkForElementDisabledAspect.class);
	
	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}
	
	@Before("@annotation(playground.aop.checkForElementDisabled) && args (element,user,..)")
	public void checkForElementDisabled(ElementEntity element, UserEntity user) throws Throwable {
		
		if(element.getType().contains("Disabled")) {
			this.log.info("*********************** You Cant Play With The Deads!");
			throw new RuntimeException("You Cant Play With The Deads!");
		}
		this.log.info("*********************** user is confirmed");	
		
	}
}
