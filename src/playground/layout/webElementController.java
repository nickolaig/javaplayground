package playground.layout;


import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.ElementTO;
import playground.logic.UserEntity;
import playground.logic.UserKey;
import playground.logic.UserService;
import playground.logic.exceptions.ElementAlreadyExistsException;
import playground.logic.exceptions.NoSuchElementID;

@RestController
public class webElementController {
	private ElementTO element;
	private ElementService elementService;

	public ElementTO getElement() {
		return element;
	}

	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@Autowired
	public void setElement(ElementTO element) {
		this.element = element;
	}

	@RequestMapping(method = RequestMethod.POST, path = "/playground/elements/{userPlayground}/{email}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO createNewElement(@RequestBody ElementTO element,
			@PathVariable("userPlayground") String userPlayground, @PathVariable("email") String email) throws Exception {
	
		ElementTO et = new ElementTO(element.getPlayground(), element.getId(), element.getLocation(), element.getName(),
				element.getCreationDate(), element.getExpirationDate(), element.getType(), element.getAttributes(),
				element.getCreatorPlayground(), element.getCreatorEmail());
		this.elementService.addNewElement(userPlayground, email, et.toEntity());
		
		return et;
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("id") String id, @RequestBody ElementTO element)
			throws Exception {
		ElementTO et = new ElementTO(element.getPlayground(), element.getId(), element.getLocation(), element.getName(),
				element.getCreationDate(), element.getExpirationDate(), element.getType(), element.getAttributes(),
				element.getCreatorPlayground(), element.getCreatorEmail());

		if (this.elementService.getElementById(userPlayground, email, et.getPlayground(), et.getId()) == null) {
			throw new Exception("No Such Element");
		} else {
			this.elementService.updateElementById(userPlayground, email, et.getPlayground(), et.getId(), et.toEntity());
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO viewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("playground") String playground,
			@PathVariable("id") String id) throws NoSuchElementID {

		if (this.elementService.getElementById(userPlayground, email, playground, id) != null) {
			ElementEntity ee = this.elementService.getElementById(userPlayground, email, playground, id);
			ElementTO et = ee.toElementTO();
			return et;
		} else {
			throw new NoSuchElementID("Element not found!");
		}

	}
	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewAllElements(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
	
		return this.elementService.getAllElements(size, page)
				.stream() 
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);
	}


	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewAllElementsClosestToDistance(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("x") double x, @PathVariable("y") double y,
			@PathVariable("distance") double distance) throws Exception {

		return this.elementService.getDistanceElements(x, y, distance);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] searchElementsByAttributeValue(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("attributeName") String attributeName,
			@PathVariable("value") String value) {
//		
//		HashMap<String, Object> attributesForCorrectCheck = new HashMap<String, Object>();
//		attributesForCorrectCheck.put("Color", "Blue");
//		
//		HashMap<String, Object> attributesForFalseCheck = new HashMap<String, Object>();
//		attributesForFalseCheck.put("Color", "Yellow");
//		
//		List<ElementTO> elements = Arrays.asList(
//				new ElementTO("Maayan", "123", new Location(), "Tamagotchi", new Date(), new Date(2020, 10, 12), "Pet", attributesForCorrectCheck , userPlayground, email),
//				new ElementTO("Maayan", "124", new Location(), "Message Board", new Date(), new Date(2020, 10, 12), "Pet", attributesForCorrectCheck , userPlayground, email),
//				new ElementTO("Maayan", "125", new Location(), "Race Car", new Date(), new Date(2020, 10, 12), "Pet", attributesForFalseCheck , userPlayground, email)
//				);
//		
//		ArrayList<ElementTO> correctElements = new ArrayList<>();
//		
//		attributesForCorrectCheck.containsKey(attributeName);
//		for(int i = 0 ; i < elements.size() ; i++) {
//			if(elements.get(i).getAttributes().containsKey(attributeName)) {
//				if(elements.get(i).getAttributes().get(attributeName).equals(value)) {
//					correctElements.add(elements.get(i));
//				}
//			}
//		}
//		
		return this.elementService.getSearch(userPlayground, email, attributeName, value);
	}


}
