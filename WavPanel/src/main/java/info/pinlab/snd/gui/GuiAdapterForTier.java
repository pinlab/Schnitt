package info.pinlab.snd.gui;

import info.pinlab.snd.trs.IntervalTier;

public interface GuiAdapterForTier<T> {
	
	public int getSelectionN();
	public IntervalSelection getSelectionX(int ix);
	
	public boolean isVisible();
	public void isVisible(boolean b);
	
	public boolean isActive();
	public boolean isEditable();
	
	
	//-- graphics
	public int getSelectionMarginTopInPx();
	public int getSelectionHeightInPx();
	
	/**
	 * @return 3 ints of RGB 
	 */
	public int[] getSelectionFillColorInRgb ();
	
	public IntervalTier<T> getTier();
	public Class<T> getTierType();
}
