package info.pinlab.snd.gui;

import info.pinlab.snd.trs.Tier;

public interface WavPanel {
	public void addTier(Tier tier);
	public void setSampleArray(int [] samples, int hz);
	
}
