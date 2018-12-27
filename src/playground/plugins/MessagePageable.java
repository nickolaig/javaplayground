package playground.plugins;

public class MessagePageable {
	private int page;
	private int size;
	
	public MessagePageable() {
		this.size = 5;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	
}

