package src.main.java.playground.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DummyElementService implements ElementService{
	private Map<String, ElementEntity> elements;
	
	@PostConstruct
	public void init() {
		this.elements =Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public ElementEntity addNewElement(ElementEntity element) {
		this.elements.put(element.getPlayground()+element.getId(), element);
		return element;
	}

	@Override
	public ElementEntity getElementById(String playground,String id) {
		ElementEntity rv = this.elements.get(playground+id);
		return rv;
	}
	public List<ElementTO> getAllElements() {
		return new ArrayList<>(
				this.elements.values())
				.stream().map(ElementEntity::toElementTO)
				.collect(Collectors.toList());
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
		List<ElementEntity> eA=(List<ElementEntity>) this.elements.values();
		
		for(int i = 0 ; i < eA.size() ; i++) {
			if(eA.get(i).getAttributes().get(attributeName)!=null) {
				if(eA.get(i).getAttributes().get(attributeName).equals(value)) {
					correctElements.add(eA.get(i).toElementTO());
				}
			}
		}
		return correctElements.toArray(new ElementTO[0]);
	}
	
	
	
}