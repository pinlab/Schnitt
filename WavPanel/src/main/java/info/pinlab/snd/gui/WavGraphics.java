package info.pinlab.snd.gui;


import java.util.ArrayList;
import java.util.List;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.Interval;
import info.pinlab.snd.trs.Tier;

public class WavGraphics implements WavPanelModel{
	
	private int [] samples;
	private double hz;
	private int viewStartSampleIx = 0;
	private int viewEndSampleIx = 0;
	private int viewSize = 0;

	//-- about the view
	private int panelWidthInPx  = 0;
	private int panelHeightInPx = 0;

	private double cursorPosInSample = 0;
//	private int cursorPosInPx = 0;
	

	private double sampleMin, sampleMax, sampleRange;
	
	private final IntervalSelection activeSelection;
	BinaryTier tier = new BinaryTier();

	
	
	public WavGraphics(){
		activeSelection = new Selection();
	}
	
	@Override
	public List<IntervalSelection> getInterVals(){
		List<IntervalSelection> intervals = new ArrayList<IntervalSelection>();
		for(int i = 0; i < tier.size();i++){
			Interval<Boolean> inter = tier.getIntervalX(i);
			if(inter!=null && inter.label!=null && inter.label){
				Selection selection = new Selection();
				selection.setSelectionStartSec(inter.startT);
				selection.setSelectionEndSec(inter.endT);
				
				intervals.add(selection);
			}
		}
		return intervals;
	}
	
	
	private class Selection implements IntervalSelection {
		double startSampleIx = 0;
		double endSampleIx = 0;
		
//		String startLabel = "";
//		String endLabel = "";
		volatile private boolean isAdjusting = false; 

		
		@Override
		public void setSelectionStartPx(int px) {
			startSampleIx = viewSize * (px /(double) panelWidthInPx);
		}
		@Override
		public void setSelectionStartSec(double s){
			startSampleIx = s*hz;
		}
		@Override
		public void setSelectionEndPx(int px) {
			endSampleIx = viewSize * (px /(double) panelWidthInPx);
		}
		public void setSelectionEndSec(double s){
			endSampleIx = s*hz;
		}

		
		
		@Override
		public int getSelectionStartPx(){
			return (int)Math.round(
					panelWidthInPx * startSampleIx / (double)viewSize
					);
		}
		public double getSelectionStartInSec(){
			return startSampleIx / hz;
		}
		@Override
		public int getSelectionEndPx() {
			return (int)Math.round(
					panelWidthInPx * endSampleIx / (double)viewSize
					);
		}
		public double getSelectionEndInSec(){
			return endSampleIx / hz ;
		}

		
		
		@Override
		public double getSelectionDurInSec() {
			return (endSampleIx - startSampleIx)/hz;
		}
		
		public void clear(){
			startSampleIx = 0;
			endSampleIx = 0;
		}
		
		@Override
		public void isAdjusting(boolean b) {
			isAdjusting = b;
		}
		
		@Override
		public boolean isAdjusting() {
			return isAdjusting;
		}
	}
	

	
	@Override
	public int getCursorPositionInPx(){
		return (int)Math.round(
				this.panelWidthInPx * cursorPosInSample / (double)this.viewSize
				);
	}
	
	@Override
	public double getCursorPositionInSec(){
		return this.getSecFromSampleX(this.cursorPosInSample);
	}
	
	
	@Override
	public void setCursorPosToMs(int ms){
		this.cursorPosInSample =  hz*(ms / 1000.0d); 
	}
	
	@Override
	public int getCursorPositionInMs(){
		return (int)Math.round(
				1000*this.cursorPosInSample / hz
				);
	}
	
	@Override
	public int getCursorPositionInSampleIx(){
		return (int) Math.round(this.cursorPosInSample);
	}
	
	@Override
	public void setCursorPosToPx(int px){
		if(px<0)px=0;
		if(px>this.panelWidthInPx)px=this.panelWidthInPx;
		cursorPosInSample = this.viewSize * (px /(double) this.panelWidthInPx);
	}
	
	@Override
	public void setCursorPosToSampleIx(int ix){
		if(ix<0)ix=0;
		this.cursorPosInSample = ix;
	}

	public double[] getWaveCurvePoints(){
		if(this.samples==null){
			return null;
		}
		if(panelWidthInPx==0){
			return null;
		}
		if(panelWidthInPx < samples.length){ //-- more samples than pixels
			return getWaveCurvePointsCondensed();
		}else{
			return getWaveCurvePointsInterpolated();
		}
	}
	

	/**
	 * Panel width in pixel >= sample length <br>
	 * 
	 * 
	 * @return x  y 
	 */
	public double[] getWaveCurvePointsInterpolated(){
		double spanSize = panelWidthInPx/(double)samples.length; 

		double [] xyCoordinates = new double[samples.length*2];
		
		for (int i = 0; i < samples.length ; i++){
			xyCoordinates[i*2+0] = (samples[i]-sampleMin)/sampleRange;
			xyCoordinates[i*2+1] = i*spanSize;
		}
		return xyCoordinates;
	}
	
	
	
	/**
	 * Panel width in pixel < sample length <br>
	 * Calculates min + max values for each point.
	 * 
	 * @return min + max values for each pixelpoint
	 */
	public double[] getWaveCurvePointsCondensed(){
		double [] minMaxCoordinates = new double [samples.length*2];
		
		double spanSize = samples.length / (double)panelWidthInPx;
		
		int j = 0;
		for(int i = 0; i < panelWidthInPx ; i++){ 
			//-- calc for each pixel: min & max sample values
			int jMax = (int)Math.round((i+1)*spanSize);

			int spanMin = samples[j];
			int spanMax = samples[j];
			while(j < jMax && j < samples.length){
				spanMin = samples[j] < spanMin ? samples[j] : spanMin; 
				spanMax = samples[j] > spanMax ? samples[j] : spanMax; 
				j++;
			}
			minMaxCoordinates[i*2+0] = this.panelHeightInPx*(spanMin-this.sampleMin)/this.sampleRange ;
			minMaxCoordinates[i*2+1] = this.panelHeightInPx*(spanMax-this.sampleMin)/this.sampleRange ;
		}
		return minMaxCoordinates;
	}
	
	
	@Override
	public void setSampleArray(int[] samples, int hz) {
		this.samples = samples;
		this.hz = hz;
		this.viewStartSampleIx = 0;
		this.viewEndSampleIx = this.samples.length;
		this.viewSize = this.viewEndSampleIx - this.viewStartSampleIx;
		
		sampleMin=sampleMax=samples[0];
		
		for(int sample : samples){
			sampleMax = (sample > sampleMax) ? sample : sampleMax;
			sampleMin = (sample < sampleMin) ? sample : sampleMin;
		}
		sampleRange = sampleMax-sampleMin;
	}

	@Override
	public int getSampleSize(){
		if( samples==null)
			return 0;
		return samples.length;
	}

	@Override
	public void setViewWidthInPx(int w){
		this.panelWidthInPx = w;
//		this.cursorPosInPx = getPxFromSampleIx(this.cursorPosInSample);
	}
	
	@Override
	public void setViewHeightInPx(int h){
		this.panelHeightInPx = h;
	}
	
	
	@Override
	public double getSecFromSampleX(double x){
		return x / hz;
	}
	
	/**
	 * Calculate frame number from horizontal pixel coordinate
	 * 
	 * @param px horizontal coordinate in pixel
	 * @return
	 */
	@Override
	public int getSampleIxFromPx(int px){
		if(this.viewSize==this.panelWidthInPx){
			return px;
		}
//		final int visibleSampleN = this.viewEndSampleIx-this.viewStartSampleIx;
				
//		System.out.println(this.viewStartSampleIx + " - " + this.viewEndSampleIx + "( " +visibleSampleN +" )");
//		System.out.println(this.viewSize *  (px /(double) this.panelWidthInPx));
		int ret = (int)Math.round(
				this.viewSize *  (px /(double) this.panelWidthInPx));
//		System.out.println(" " + px + " > px > sample > " + ret);
		return ret;
	}

	
	@Override
	public double getSecFromPx(int px){
		final int visibleSampleN = (this.viewEndSampleIx-this.viewStartSampleIx);
		return (visibleSampleN *  (px /(double) this.panelWidthInPx))
				/ hz  // -- in seconds
				;
	}

	
	@Override
	public int getPxFromSampleIx(int x){
//		System.out.println(x + " > sample > px > " + this.panelWidthInPx * (x / (double)this.viewSize));
		return (int)Math.round(this.panelWidthInPx * x / (double)this.viewSize);
	}
	
	

	@Override
	public void addTier(Tier tier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IntervalSelection getActiveIntervalSelection() {
		return activeSelection;
	}

	
	
	
	@Override
	public void addIntervalSelection(IntervalSelection selection) {
		Interval<Boolean> inter = new Interval<Boolean>(
				selection.getSelectionStartInSec()
				,selection.getSelectionEndInSec(), true);
		tier.addInterval(inter);
	}

}
