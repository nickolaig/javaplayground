package playground.plugins;

public class MessagePageableByElementId {
	private String page;
	private String size;
	private String elementId=null;
	public MessagePageableByElementId() {
		this.size = "10";
		this.page = "0";
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	
}

