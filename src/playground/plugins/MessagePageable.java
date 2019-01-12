package playground.plugins;

public class MessagePageable {
	private String page;
	private String size;
	
	public MessagePageable() {
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
	
	
}

