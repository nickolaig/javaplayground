package src.main.java.playground.logic;

import java.util.Collection;
import java.util.List;

public interface ElementService {
	public void cleanup();
	public ElementEntity addNewElement(ElementEntity element);
	public ElementEntity getElementById(String playground, String id);
	public void updateElementById(String playground, String id,ElementEntity element);
	public List<ElementTO> getAllElements();
	public int size();
	public ElementTO[] getSearch(String attributeName,String value);
	public ElementTO[] getDistanceElements(double x,double y,double distance) throws Exception;
	public ElementTO[] getElementsWithPagination(int size, int page);
}
