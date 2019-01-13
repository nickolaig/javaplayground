package playground.plugins;

public class Tamagotchi {
	private Integer Life;
	private Integer Happiness;
	private Integer Fed;
	private Boolean isAlive;

	public Tamagotchi() {
		
	}
	public Boolean getIsAlive() {
		return isAlive;
	}
	public void setIsAlive(Boolean isAlive) {
		this.isAlive = isAlive;
	}
	public Integer getLife() {
		return Life;
	}

	public void setLife(Integer life) {
		this.Life = life;
	}
	public void increaseLife(Integer life) {
		if(this.Life+life>100)
			this.Life=100;
		else
			if(this.Life+life<=0)
			{
				this.Life=0;
				this.isAlive=false;
			}
			else
				this.Life +=life;
		if (life<0)
			this.increaseHappiness(-10);
	}
	public Integer getHappiness() {
		return this.Happiness;
	}

	public void setHappiness(Integer happiness) {
		this.Happiness = happiness;
	}
	public void increaseHappiness(Integer hapiness) {
		if(this.Happiness+hapiness>100)
			this.Happiness=100;
		else
			if(this.Happiness+hapiness<=0)
			{
				this.Happiness=30;
				this.increaseLife(-20);
			}
			else
				this.Happiness +=hapiness;
	}

	public Integer getFed() {
		return this.Fed;
	}

	public void setFed(Integer fed) {
		this.Fed = fed;
	}
	public void increaseFed(Integer fed) {
		if(this.Fed+fed>100)
			{
			this.Fed=100;
			this.increaseLife(-10);
			this.increaseHappiness(-5);
			}
		else
			if(this.Fed+fed<=0)
			{
				this.Fed=10;
				this.increaseLife(-80);
				this.increaseHappiness(-50);
			}
			else
				this.Fed +=fed;
	}

	
}
