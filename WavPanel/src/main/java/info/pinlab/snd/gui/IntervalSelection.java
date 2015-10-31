package info.pinlab.snd.gui;

public interface IntervalSelection {
	
	public void setSelectionStartPx(int px);
	public void setSelectionStartSec(double s);
	public void setSelectionEndPx(int px);
	public void setSelectionEndSec(double s);

	
	public int getSelectionStartPx();
	public int getSelectionEndPx();
	public double getSelectionStartInSec();
	public double getSelectionEndInSec();
	
	public double getSelectionDurInSec();
	
	
	
	public void isAdjusting(boolean b);
	public boolean isAdjusting();
	public void clear();
	
}
