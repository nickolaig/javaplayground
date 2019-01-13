package playground.jpaLogic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.CheckValidActionByRule;
import playground.aop.MyLog;
import playground.aop.ValidationManagerLog;
import playground.aop.checkForUserConfirmation;
import playground.dal.ElementDao;
import playground.dal.IdGeneratorDao;
import playground.logic.ElementEntity;
import playground.logic.ElementKey;
import playground.logic.ElementService;
import playground.logic.ElementTO;
import playground.logic.Location;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.exceptions.ElementAlreadyExistsException;
import playground.logic.exceptions.InvalidInputException;
import playground.logic.exceptions.NoSuchElementID;
import playground.plugins.ElementMsgColor;

@Service
public class JpaElementService implements ElementService {

	private ElementDao elements;
	private UserService users;
	private IdGeneratorDao idGenerator;
	private final static String PLAYGROUND_NAME="TA";
	
	@Autowired
	public JpaElementService(ElementDao elements, UserService users, IdGeneratorDao idGenerator) {
		this.elements = elements;
		this.users = users;
		this.idGenerator = idGenerator;
	}

	@Override
	@Transactional
	@MyLog
	@checkForUserConfirmation
	@CheckValidActionByRule(role="Player")
	public ElementEntity addNewElement(String userPlayground, String email, ElementEntity element) {
			IdGenerator tmp = this.idGenerator.save(new IdGenerator());
			Long dummyId = tmp.getId();
			this.idGenerator.delete(tmp);

			element.setPlaygroundAndID(new ElementKey(dummyId.toString(), PLAYGROUND_NAME));
			element.setCreatorEmail(email);
			element.setCreatorPlayground(userPlayground);
			
			switch (element.getType()) {
				case "message_board":
				ElementMsgColor elementMsgCol=new ElementMsgColor();
				element.getAttributes().put("msgColor",elementMsgCol.getMsgColor());
					break;
				case "Tamagotchi":
								element.getAttributes().put("life",50);
								element.getAttributes().put("happiness", 50);
								element.getAttributes().put("fed", 50);
								element.getAttributes().put("isAlive", true);
								
					break;
			}
			
			
			return this.elements.save(element);
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	
	public ElementEntity getElementById(String userPlayground, String email, String playground, String id) throws NoSuchElementID {

		// ElementEntity rv = this.elements.get(playground+id);
		ElementEntity rv = this.elements.findById(new ElementKey(id, userPlayground)).orElseThrow(() -> new NoSuchElementID("No element with " + id));
		return rv;
	}

	@Override
	@Transactional
	@MyLog
	@checkForUserConfirmation
	public void updateElementById(String userPlayground, String email, String playground, String id, ElementEntity element, boolean afterInvokeOperation) throws Exception {
		UserEntity user = this.users.getUserByEmailAndPlayground(new UserKey(email, playground));
		
		if(!afterInvokeOperation && user.getRole().equalsIgnoreCase("player")) {
			throw new RuntimeException("Player Cant Update Elements!");
		}
		ElementEntity existingElement = this.getElementById(userPlayground, email, playground, id);
		
		if (element.getName() != null && !element.getName().equals(existingElement.getName())) {
			existingElement.setName(element.getName());
		}
		
		if (element.getExpirationDate() != null
				&& !element.getExpirationDate().equals(existingElement.getExpirationDate())) {
			existingElement.setExpirationDate(element.getExpirationDate());
		}
		
		if (element.getX() != null && !element.getX().equals(existingElement.getX())) {
			existingElement.setX(element.getX());
		}
		
		if (element.getY() != null && !element.getY().equals(existingElement.getY())) {
			existingElement.setY(element.getY());
		}
		
		if (element.getAttributes() != null) {
			existingElement.setAttributes(element.getAttributes());
		}
		
		if(element.getPlaygroundAndID() != null && !element.getPlaygroundAndID().equals(existingElement.getPlaygroundAndID())) {
			throw new RuntimeException("Cant update element's playground/id");
		}

		this.elements.save(existingElement);
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	@checkForUserConfirmation
	public List<ElementEntity> getAllElements(String userPlayground, String userEmail, int size, int page) throws Exception {
		
		UserEntity user = this.users.getUserByEmailAndPlayground(new UserKey(userEmail, userPlayground));
		
		if (user.getRole().equals("Manager")) {
			return this.elements.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
		} else {
			Date currentDate = new Date();
			return this.elements.findAllByExpirationDateAfterOrExpirationDateIsNull(currentDate,
					PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	@checkForUserConfirmation
	@MyLog
	public List<ElementEntity> getNearElements(String userPlayground, String email, int size, int page, double x,
			double y, double distance) throws Exception {

		UserEntity user = this.users.getUserByEmailAndPlayground(new UserKey(email,userPlayground));
		if(distance<0)throw new Exception("Negative distance!");
		if (user.getRole().equals("Manager")) {
			return this.elements.findAllByXBetweenAndYBetween(x - distance, x + distance, y - distance,
					y + distance, PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
		}

		else {
			Date currentDate = new Date();
			return this.elements.findAllByXBetweenAndYBetweenAndExpirationDateAfterOrExpirationDateIsNull(x - distance,
					x + distance, y - distance, y + distance, currentDate,
					PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
		}
	}
	
	@MyLog
	private static Boolean checkDistance(double x, double y, double distance, Location location) {
		double currentDistance;

		currentDistance = Math.sqrt(Math.pow(x - location.getX(), 2) + Math.pow(y - location.getY(), 2));
		return currentDistance <= distance;
	}

	/*
	 * @Override public ElementTO[] getSearch(String attributeName, String value) {
	 * // TODO Auto-generated method stub return null; }
	 * 
	 */
	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.elements.deleteAll();

	}

	//@ValidationNotNullPlaygroundAndEmail
	//@CheckConfirmUser
	@Override
	@Transactional(readOnly = true)
	@MyLog
	public List<ElementEntity> searchElementsByAttributeOrType(String userPlayground, String email,String attributeName, String value, int size, int page) throws Exception {

		UserEntity user = this.users.getUserByEmailAndPlayground(new UserKey(email,userPlayground));
		String role = user.getRole();
		Date currentDate = new Date();
		
		if (attributeName.equals("Name")) {
			if(role.equals("Manager"))
			return this.elements.findAllByNameLike(value, PageRequest.of(page, size, Direction.DESC, "creationDate"))
					.getContent();
			else 
				return this.elements.findAllByNameLikeAndExpirationDateAfterOrExpirationDateIsNull(value, currentDate,
						PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
			
		} else if (attributeName.equals("type")) {
			if(role.equals("Manager")) {
			return elements.findAllByTypeLike(value, PageRequest.of(page, size, Direction.DESC, "creationDate"))
					.getContent();
			}
			else 
				return this.elements.findAllByTypeLikeAndExpirationDateAfterOrExpirationDateIsNull(value,currentDate ,
						PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
		}

		throw new InvalidInputException("attribute name not found" + attributeName);
	}
}
