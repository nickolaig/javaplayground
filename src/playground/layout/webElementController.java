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
		return new ElementTO(this.elementService.addNewElement(userPlayground, email, et.toEntity()));
		
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("id") String id, @PathVariable("playground") String playground, @RequestBody ElementTO element)
			throws Exception {
		
		ElementTO et = new ElementTO(element.getPlayground(), element.getId(), element.getLocation(), element.getName(),
				element.getCreationDate(), element.getExpirationDate(), element.getType(), element.getAttributes(),
				element.getCreatorPlayground(), element.getCreatorEmail());
		this.elementService.updateElementById(userPlayground, email, playground, id, element.toEntity(),false);
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
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
	
		return this.elementService.getAllElements(userPlayground,email,size, page)
				.stream() 
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);
	}


	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewAllElementsClosestToDistance(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("x") double x,
			@PathVariable("y") double y,
			@PathVariable("distance") double distance,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		
		return this.elementService.getNearElements(userPlayground,email,size,page,x,y,distance)
				.stream()
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);

	
	}

	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}", produces = MediaType.APPLICATION_JSON_VALUE)
			public ElementTO[] searchElementsByAttributeOrType(@PathVariable("userPlayground") String userPlayground,
						@PathVariable("email") String email ,
						@PathVariable("attributeName") String attributeName,
						@PathVariable("value") String value,
						@RequestParam(name="size", required=false, defaultValue="10") int size, 
						@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception{
				return this.elementService.searchElementsByAttributeOrType(userPlayground,email,attributeName ,value , size , page)
						.stream()
						.map(ElementTO::new)
						.collect(Collectors.toList())
						.toArray(new ElementTO[0]);

	}


}
