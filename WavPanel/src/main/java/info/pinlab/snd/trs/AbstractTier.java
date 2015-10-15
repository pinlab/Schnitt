package info.pinlab.snd.trs;

public abstract class AbstractTier implements Tier{
	private double start = 0.0f;
	private String name ="";

	public double getTierStartTime(){
		return this.start;
	}
	public void setTierStartTime(double t){
		this.start = t;
	}
	
	public String getName(){
		return this.name; 
	}
	public void setName(String name){
		this.name = name;
	}
	
}
