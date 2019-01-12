package playground.plugins;

public class ElementMsgColor {

	
	private String msgColor;
	private final String[] colors= {"Red","Green","Blue","Purple","White"};
	public ElementMsgColor() {
		this.msgColor=colors[(int) (Math.random()*colors.length)];
	}

	
	public ElementMsgColor(String msgColor) {
		this.msgColor = msgColor;
	}


	public String getMsgColor() {
		return this.msgColor;
	}

	public void setMsgColor(String msgColor) {
		this.msgColor = msgColor;
	}
	
	
}
