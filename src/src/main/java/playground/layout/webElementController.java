package src.main.java.playground.layout;
//TODO ADD PAGINATION!
//TODO REFACTOR CODE TO SERVICE CLASS DO NOT OVERLOAD THE CONTROLLER 
//TODO ADD APPROPRIATE EXCEPTION CLASSES
//TODO REWORK ATTRIBUTE SEARCH MANAGER
//TODO CHECK GHERKIN FOR TEST COVERAGE (WHAT HAPPENS IF SAME USER ADDED TWICE?)
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import src.main.java.playground.logic.ElementEntity;
import src.main.java.playground.logic.ElementService;
import src.main.java.playground.logic.ElementTO;
import src.main.java.playground.logic.Location;
import src.main.java.playground.logic.Message;
import src.main.java.playground.logic.NewUserForm;

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
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/elements/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO createNewElement(@RequestBody ElementTO element, @PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) {
		ElementTO et=new ElementTO(element.getPlayground(), element.getId(),element.getLocation(), element.getName(),element.getCreationDate(),
				element.getExpirationDate(),element.getType(),element.getAttributes(),
				element.getCreatorPlayground(), element.getCreatorEmail());
		this.elementService.addNewElement(et.toEntity());
		return et;
	}
	
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("id") String id,
			@RequestBody ElementTO element) throws Exception {
		ElementTO et=new ElementTO(element.getPlayground(), element.getId(),element.getLocation(), element.getName(),element.getCreationDate(),
				element.getExpirationDate(),element.getType(),element.getAttributes(),
				element.getCreatorPlayground(), element.getCreatorEmail());

		
		if(this.elementService.getElementById(et.getPlayground(), et.getId())==null) {
			throw new Exception("No Such Element");
		}
		else
		{
			this.elementService.updateElementById(et.getPlayground(),et.getId(), et.toEntity());
		}
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO viewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) throws Exception {
		
		if(this.elementService.getElementById(playground, id)!=null) {
			ElementEntity ee=this.elementService.getElementById(playground, id);
			ElementTO et=ee.toElementTO();
			return et;
		} else {
			throw new Exception("Element not found!");
		}
		
	}
	//TODO ADD PAGINATION!
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/all",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewAllElements(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) {
		
		List<ElementTO> elements = this.elementService.getAllElements();
		
		return elements.toArray(new ElementTO[0]);
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewAllElementsClosestToDistance(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("x") double x,
			@PathVariable("y") double y,
			@PathVariable("distance") double distance) throws Exception {
		//TODO redo to service
		List<ElementTO> elements = this.elementService.getAllElements();
		
		ArrayList<ElementTO> correctElements = new ArrayList<>();
		System.out.println(elements);
		
		if(distance <= 0) {
			throw new Exception("Negative distance");
		}
		for(int i = 0 ; i < elements.size() ; i++) {
			if(checkDistance(x, y, distance, elements.get(i).getLocation())) {
				correctElements.add(elements.get(i));
			}
		}
		
		return correctElements.toArray(new ElementTO[0]);
	}

	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] searchElementsByAttributeValue(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("attributeName") String attributeName,
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
		return this.elementService.getSearch(attributeName, value);
	}
	
	public static Boolean checkDistance(double x, double y, double distance, Location location) {
		double currentDistance;
		
		currentDistance = Math.sqrt(Math.pow(x - location.getX(), 2) + Math.pow(y - location.getY(), 2));
		return currentDistance <= distance;		
	}
	
}
