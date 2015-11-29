package info.pinlab.snd.gui;

import info.pinlab.snd.trs.Interval;

public interface GuiAdapterForBinaryTier extends GuiAdapterForTier<Boolean> {
	public void addInterval(Interval<Boolean> interval);
}
