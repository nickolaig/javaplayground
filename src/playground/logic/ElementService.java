package playground.logic;

import java.util.List;

import playground.logic.exceptions.ElementAlreadyExistsException;
import playground.logic.exceptions.NoSuchElementID;

public interface ElementService {
	public void cleanup();
	public ElementEntity addNewElement(ElementEntity element) throws ElementAlreadyExistsException;
	public ElementEntity getElementById(String playground, String id) throws NoSuchElementID;
	public void updateElementById(String playground, String id,ElementEntity element) throws NoSuchElementID;
	public ElementTO[] getSearch(String attributeName,String value);
	public ElementTO[] getDistanceElements(double x,double y,double distance) throws Exception;
	public List<ElementEntity> getAllElements(int size, int page);
}
