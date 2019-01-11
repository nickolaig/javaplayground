package playground.jpaLogic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.MyLog;
import playground.aop.ValidationManagerLog;
import playground.dal.ElementDao;
import playground.dal.IdGeneratorDao;
import playground.logic.ElementEntity;
import playground.logic.ElementKey;
import playground.logic.ElementService;
import playground.logic.ElementTO;
import playground.logic.Location;
import playground.logic.exceptions.ElementAlreadyExistsException;
import playground.logic.exceptions.NoSuchElementID;

@Service
public class JpaElementService implements ElementService {

	private ElementDao elements;
	private IdGeneratorDao idGenerator;
	private final static String PLAYGROUND_NAME="TA";
	@Autowired
	public JpaElementService(ElementDao elements, IdGeneratorDao idGenerator) {
		this.elements = elements;
		this.idGenerator = idGenerator;
	}

	@Override
	@Transactional
	@MyLog
	@ValidationManagerLog
	public ElementEntity addNewElement(String userPlayground, String email, ElementEntity element) throws ElementAlreadyExistsException {
			IdGenerator tmp = this.idGenerator.save(new IdGenerator());
			Long dummyId = tmp.getId();
			this.idGenerator.delete(tmp);

			element.setPlaygroundAndID(new ElementKey(PLAYGROUND_NAME,dummyId.toString()));
			element.setCreatorEmail(email);
			element.setCreatorPlayground(userPlayground);
			
			
			
			System.err.println("IN GENERATOR ID: " + element.getPlaygroundAndID().getId());
			return this.elements.save(element);
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	public ElementEntity getElementById(String userPlayground, String email, String playground, String id) throws NoSuchElementID {

		// ElementEntity rv = this.elements.get(playground+id);
		ElementEntity rv = this.elements.findById(id).orElseThrow(() -> new NoSuchElementID("No element with " + id));
		return rv;
	}

	@Override
	@Transactional
	@MyLog
	public void updateElementById(String userPlayground, String email, String playground, String id, ElementEntity element) throws NoSuchElementID {

		ElementEntity existingElement = this.getElementById(userPlayground, email, playground, id);

		if (element.getX() != null && !element.getX().equals(existingElement.getX())) {
			existingElement.setX(element.getX());
		}
		if (element.getY() != null && !element.getY().equals(existingElement.getY())) {
			existingElement.setY(element.getY());
		}
		if (element.getName() != null && !element.getName().equals(existingElement.getName())) {
			existingElement.setName(element.getName());
		}
		if (element.getCreatorEmail() != null && !element.getCreatorEmail().equals(existingElement.getCreatorEmail())) {
			existingElement.setCreatorEmail(element.getCreatorEmail());
		}
		if (element.getAttributes() != null && !element.getAttributes().equals(existingElement.getAttributes())) {
			existingElement.setAttributes(element.getAttributes());
		}
		if (element.getCreatorPlayground() != null
				&& !element.getCreatorPlayground().equals(existingElement.getCreatorPlayground())) {
			existingElement.setCreatorPlayground(element.getCreatorPlayground());
		}
		if (element.getExpirationDate() != null
				&& !element.getExpirationDate().equals(existingElement.getExpirationDate())) {
			existingElement.setExpirationDate(element.getExpirationDate());
		}
		if (element.getPlayground() != null && !element.getPlayground().equals(existingElement.getPlayground())) {
			existingElement.setPlayground(element.getPlayground());
		}

		this.elements.save(existingElement);
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	public List<ElementEntity> getAllElements(int size, int page) {
		return this.elements.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	public ElementTO[] getDistanceElements(double x, double y, double distance) throws Exception {
		ArrayList<ElementTO> correctElements = new ArrayList<>();

		if (distance <= 0) {
			throw new Exception("Negative distance");
		}
		List<ElementEntity> partition = getAllElements(Integer.MAX_VALUE, 0);
		for (ElementEntity element : partition) {
			if (element.getX() != null && element.getY() != null)
				if (checkDistance(x, y, distance, new Location(element.getX(), element.getY()))) {
					correctElements.add(element.toElementTO());
				}
		}
		return correctElements.toArray(new ElementTO[0]);
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

	@Override
	@Transactional
	@MyLog
	public ElementTO[] getSearch(String userPlayground, String email, String attributeName, String value) {

		ArrayList<ElementTO> correctElements = new ArrayList<>();
		Iterable<ElementEntity> eA = this.elements.findAll();
		for (ElementEntity e : eA) {
			switch (attributeName) {
			case "playground":
				if (e.getPlayground().equals(value))
					correctElements.add(e.toElementTO());
				break;
			case "id":
				if (e.getId().equals(value))
					correctElements.add(e.toElementTO());
				break;
			case "name":
				if (e.getName().equals(value))
					correctElements.add(e.toElementTO());
				break;
			case "creationDate":
				if (e.getCreationDate().toString().equals(value))
					correctElements.add(e.toElementTO());
				break;
			case "expirationDate":
				if (e.getExpirationDate().toString().equals(value))
					correctElements.add(e.toElementTO());
				break;
			case "creatorPlayground":
				if (e.getCreatorPlayground().equals(value))
					correctElements.add(e.toElementTO());
				break;
			case "creatorEmail":
				if (e.getCreatorEmail().equals(value))
					correctElements.add(e.toElementTO());
				break;
			case "x":
				if (e.getX().toString().equals(value))
					correctElements.add(e.toElementTO());
				break;
			case "y":
				if (e.getY().toString().equals(value))
					correctElements.add(e.toElementTO());
				break;
			default:
				if (e.getAttributes() != null)
					if (e.getAttributes().get(attributeName) != null)
						if (e.getAttributes().get(attributeName).equals(value)) {
							correctElements.add(e.toElementTO());
						}
			}

		}
		return correctElements.toArray(new ElementTO[0]);
	}

}
