package info.pinlab.schnitt.gui;

import info.pinlab.snd.trs.IntervalTier;

public interface GuiAdapterForTier<T> {
	
	public int getSelectionN();
	public IntervalSelection getSelectionX(int ix);
	
	public boolean isVisible();
	public void isVisible(boolean b);
	
	public boolean isActive();
	public boolean isEditable();
	
	
	//-- graphics
	public int getSelectionYTop();
	public int getSelectionYBottom();
	
	/**
	 * @return 3 ints of RGB 
	 */
	public int[] getSelectionFillColorInRgb ();
	
	public IntervalTier<T> getTier();
	public Class<T> getTierType();

	
	/**
	 * returns null if there is no selection under co-ordinate X
	 * 
	 * @param x
	 * @return
	 */
	public IntervalSelection getSelectionForX(int x);
	
	
	public void refreshSelection();
	
}
