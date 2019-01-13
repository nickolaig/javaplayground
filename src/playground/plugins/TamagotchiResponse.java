package playground.plugins;

public class TamagotchiResponse {

	private Integer Life;
	private Integer Happiness;
	private Integer Fed;
	private Integer Points;
	private Boolean isAlive;

	public TamagotchiResponse() {
	 
	}
	public TamagotchiResponse(Integer life, Integer happiness, Integer fed, Integer points,Boolean isAlive) {
		super();
		Life = life;
		Happiness = happiness;
		Fed = fed;
		Points = points;
		this.isAlive=isAlive;
	}
	public Integer getLife() {
		return Life;
	}
	public void setLife(Integer life) {
		Life = life;
	}
	public Integer getHappiness() {
		return Happiness;
	}
	public void setHappiness(Integer happiness) {
		Happiness = happiness;
	}
	public Integer getFed() {
		return Fed;
	}
	public void setFed(Integer fed) {
		Fed = fed;
	}
	public Integer getPoints() {
		return Points;
	}
	public void setPoints(Integer points) {
		Points = points;
	}
	public Boolean getIsAlive() {
		return isAlive;
	}
	public void setIsAlive(Boolean isAlive) {
		this.isAlive = isAlive;
	}

}
