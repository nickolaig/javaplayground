package src.main.java.playground.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import src.main.java.playground.logic.ElementTO;
import src.main.java.playground.logic.Location;
import src.main.java.playground.logic.Message;
import src.main.java.playground.logic.NewUserForm;
import src.main.java.playground.logic.UserTO;

@RestController
public class webElementController {
	private ElementTO element;
	
	public ElementTO getElement() {
		return element;
	}
	
	@Autowired
	public void setElement(ElementTO element) {
		this.element = element;
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/elements/{userPlayground }/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO createNewElement(@RequestBody ElementTO element, @PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) {
		return new ElementTO();
	}
	
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("id") String id,
			@RequestBody ElementTO element) {
		//TODO
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO viewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) throws Exception {
		
		if(id.startsWith("1")) {
			return new ElementTO(playground, id, new Location(), "Tamagotchi", new Date(), new Date(2020, 10, 12), "Pet", new HashMap<>(), userPlayground, email);
		} else {
			throw new Exception("Element not found!");
		}
		
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/all",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewAllElements(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) {
		
		List<ElementTO> elements = Arrays.asList(
				new ElementTO("Maayan", "123", "Tamagotchi", userPlayground, email),
				new ElementTO("Maayan", "124", "Message Board", userPlayground, email),
				new ElementTO("Maayan", "125", "Race Car", userPlayground, email)
				);
		
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
			@PathVariable("distance") double distance) {
		
		ElementTO[] elements = null;
		return elements;
	}

	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] searchElementsByAttributeValue(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("attributeName") String attributeName,
			@PathVariable("value") String value) {
		
		HashMap<String, Object> attributesForCorrectCheck = new HashMap<String, Object>();
		attributesForCorrectCheck.put("Color", "Blue");
		
		HashMap<String, Object> attributesForFalseCheck = new HashMap<String, Object>();
		attributesForFalseCheck.put("Color", "Yellow");
		
		List<ElementTO> elements = Arrays.asList(
				new ElementTO("Maayan", "123", new Location(), "Tamagotchi", new Date(), new Date(2020, 10, 12), "Pet", attributesForCorrectCheck , userPlayground, email),
				new ElementTO("Maayan", "124", new Location(), "Message Board", new Date(), new Date(2020, 10, 12), "Pet", attributesForCorrectCheck , userPlayground, email),
				new ElementTO("Maayan", "125", new Location(), "Race Car", new Date(), new Date(2020, 10, 12), "Pet", attributesForFalseCheck , userPlayground, email)
				);
		
		ArrayList<ElementTO> correctElements = new ArrayList<>();
		
		attributesForCorrectCheck.containsKey(attributeName);
		for(int i = 0 ; i < elements.size() ; i++) {
			if(elements.get(i).getAttributes().containsKey(attributeName)) {
				if(elements.get(i).getAttributes().get(attributeName).equals(value)) {
					correctElements.add(elements.get(i));
				}
			}
		}
		
		return correctElements.toArray(new ElementTO[0]);
	}
	
}
