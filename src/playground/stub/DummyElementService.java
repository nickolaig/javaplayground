
/*This Class is deprecated and will be completely removed


package playground.stub;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.ElementTO;
import playground.logic.Location;

//@Service
public class DummyElementService implements ElementService{
	private Map<String, ElementEntity> elements;
	
	@PostConstruct
	public void init() {
		this.elements =Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public ElementEntity addNewElement(ElementEntity element) {
		if(this.elements.containsKey(element.getPlayground()+element.getId()))
			throw new RuntimeException("Element already exists");
		this.elements.put(element.getPlayground()+element.getId(), element);
		return element;
	}

	@Override
	public ElementEntity getElementById(String playground,String id) {
		ElementEntity rv = this.elements.get(playground+id);
		return rv;
	}
	public ElementTO[] getAllElements() {
		return new ArrayList<>(
				this.elements.values())
				.stream().map(ElementEntity::toElementTO)
				.collect(Collectors.toList()).toArray(new ElementTO[0]);
	}

	@Override
	public void cleanup() {
		elements.clear();
	}

	@Override
	public int size() {
		return this.elements.size();
	}

	@Override
	public void updateElementById(String playground, String id, ElementEntity element) {
		synchronized(this.elements){
		if(!this.elements.containsKey(playground+id))
			throw new RuntimeException("Element not found!!1");
		this.elements.put(playground+id,element);

		}
	}

	@Override
	public ElementTO[] getSearch(String attributeName, String value) {
		ArrayList<ElementTO> correctElements = new ArrayList<>();
		Collection<ElementEntity> eA = this.elements.values();
		for (ElementEntity e : eA) {
			if (e.getAttributes().get(attributeName) != null)
					if (e.getAttributes().get(attributeName).equals(value)) {
						correctElements.add(e.toElementTO());
					}
		}
		return correctElements.toArray(new ElementTO[0]);
	}

	@Override
	public ElementTO[] getDistanceElements(double x, double y, double distance) throws Exception {
		
		
		ArrayList<ElementTO> correctElements = new ArrayList<>();
		System.out.println();

		if (distance <= 0) {
			throw new Exception("Negative distance");
		}
		for (ElementEntity element:this.elements.values()) {
			if (checkDistance(x, y, distance, element.getLocation())) {
				correctElements.add(element.toElementTO());
			}
		}
		return correctElements.toArray(new ElementTO[0]);
	}
	public static Boolean checkDistance(double x, double y, double distance, Location location) {
		double currentDistance;

		currentDistance = Math.sqrt(Math.pow(x - location.getX(), 2) + Math.pow(y - location.getY(), 2));
		return currentDistance <= distance;
	}

	@Override
	public ElementTO[] getElementsWithPagination(int size, int page) {
		return (new ArrayList<>(
				this.elements.values())
				.stream()
				.skip(size * page)
				.limit(size).map(ElementEntity::toElementTO)
				.collect(Collectors.toList())).toArray(new ElementTO[0]);
	}

	
	
	
}
 */