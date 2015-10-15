package info.pinlab.snd.gui;

import java.util.HashMap;
import java.util.Map;

import info.pinlab.snd.trs.Tier;

public class WavPanelControl {
	private WavPanel view = null;
	private Map<String, Tier> tiers = new HashMap<String, Tier>();
	
	
	public WavPanelControl(){
		
	}
	
	public void setWavPanel(WavPanel view){
		this.view = view;
	}
	

	public void addTier(Tier tier){
		String tierName = tier.getName();
		if(tierName==null || tierName.isEmpty()){
			tierName = String.format("tier%02d", tiers.size());
		}
		if(tiers.containsKey(tierName)){
			throw new IllegalStateException("Already defined tier! '" + tierName +"'");
		}
		tiers.put(tierName, tier);
		if(view != null){
			view.addTier(tier);
		}
	}
	

	
	
	
	public static void main(String[] args) {
	}
}
