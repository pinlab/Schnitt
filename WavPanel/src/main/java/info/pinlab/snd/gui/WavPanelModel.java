package info.pinlab.snd.gui;

import java.util.List;

import info.pinlab.snd.trs.Interval;
import info.pinlab.snd.trs.IntervalTier;
import info.pinlab.snd.trs.Tier;

public interface WavPanelModel {
//	public void addTier(Tier tier);
	public void setSampleArray(int [] samples, int hz);
	public int getSampleSize();
	
	public void setViewWidthInPx(int w);
	public void setViewHeightInPx(int h);
	
	public double[] getWaveCurvePointsCondensed();
	public double[] getWaveCurvePointsInterpolated();

	public int getSampleIxFromPx(int px);
	public double getSecFromPx(int px);
	
	public void setCursorPosToPx(int px);
	public void setCursorPosToMs(int ms);
	public void setCursorPosToSampleIx(int ix);

	public int getCursorPositionInSampleIx();
	public int getCursorPositionInPx();
	public double getCursorPositionInSec();
	public int getCursorPositionInMs();
	
	public double getSecFromSampleX(double x);
	public int getPxFromSampleIx(int x);

	//-- Intervals
	public int addTier(IntervalTier<?> t);
	public int getTierN();
	public Tier getTierByIx(int ix);
	
	
	
	//-- Active Selection getters
//	public boolean hasActiveSelection();
	public IntervalSelection getActiveIntervalSelection();
	public void addIntervalSelection(IntervalSelection selection);
	public void addInterval(Interval<?> interval);

	public List<IntervalSelection> getInterVals();
	public List<IntervalSelection> getInterVals(int tierIx);


	//-- ZOOM
	public void zoomTo(double start, double end);
	public void zoomOut();
}
