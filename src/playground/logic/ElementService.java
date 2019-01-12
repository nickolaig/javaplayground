package playground.logic;


import java.util.List;


import playground.logic.exceptions.NoSuchElementID;

public interface ElementService {
	public void cleanup();
	public ElementEntity addNewElement(String userPlayground, String email, ElementEntity element);
	public ElementEntity getElementById(String userPlayground, String email, String playground, String id) throws NoSuchElementID;
	public void updateElementById(String userPlayground, String email, String playground, String id,ElementEntity element, boolean afterInvokeOperation) throws Exception;
	public List<ElementEntity> getAllElements(String userPlayground, String userEmail, int size, int page) throws Exception;
	public List<ElementEntity> getNearElements(String userPlayground, String email, int size, int page, double x,double y, double distance)throws Exception;
	public List<ElementEntity> searchElementsByAttributeOrType(String userPlayground, String email, String attributeName,
			String value, int size, int page) throws Exception;
}
