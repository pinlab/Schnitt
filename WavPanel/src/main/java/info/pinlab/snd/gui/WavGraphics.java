package info.pinlab.snd.gui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.Interval;
import info.pinlab.snd.trs.IntervalTier;
import info.pinlab.snd.trs.Tier;
import info.pinlab.snd.trs.VadErrorTier;
import info.pinlab.snd.vad.VadError;

public class WavGraphics implements WavPanelModel{
	public static Logger LOG = LoggerFactory.getLogger(WavGraphics.class);
	
	private int [] samples;
	private double hz;
	private int viewStartSampleIx = 0;
	private int viewEndSampleIx = 0;
	private int viewSizeInSample = 0;

	//-- about the view
	private int panelWidthInPx  = 0;
	private int panelHeightInPx = 0;

	private double cursorPosInSample = 0;
//	private int cursorPosInPx = 0;
	

	private double sampleMin, sampleMax, sampleMean, sampleRange;
	private int sampleSum, sampleHalfRange, sampleRangeUpper, sampleRangeLower;
	int sampleFreqMax = 0;
	int sampleFreqMaxVal = 0; 

	
	private int tierN = 0;
	private int editableTierIx = 0;
	
	private final IntervalSelection activeSelection;
//	BinaryTier tier = new BinaryTier();

	Map<Integer, TierAdapter<?>> tiers = new HashMap<Integer, WavGraphics.TierAdapter<?>>();
	
	
	class TierAdapter<T>{
		private final IntervalTier<T> tier;
		boolean isVisible = true;
		String label;
		
		int selectionMarginTop = 50; /*px*/
		int selectionHeight    = 40; /*px*/
		
		TierAdapter(IntervalTier<T> t){
			//			this.id=cnt++;
			this.tier = t;
		}
		
		synchronized List<IntervalSelection> getIntervals(){
			List<IntervalSelection> intervals = new ArrayList<IntervalSelection>();
			if(this.tier instanceof BinaryTier){
				BinaryTier btier = (BinaryTier) tier;
				for(int i = 0; i < tier.size();i++){
					 Interval<Boolean> inter = btier.getIntervalX(i);
					if(inter!=null && inter.label!=null && inter.label){
						Selection selection = new Selection();
						selection.setSelectionStartSec(inter.startT);
						selection.setSelectionEndSec(inter.endT);
						
						intervals.add(selection);
					}
				}
			}//-- if NOT binary tier...
			return intervals;
		}
		
	}

	
	
	
	public WavGraphics(){
		activeSelection = new Selection();
		BinaryTier tier = new BinaryTier();
		editableTierIx = addTier(tier);
	}
	

	
	@Override
	public List<IntervalSelection> getInterVals(){
		return getInterVals(editableTierIx);
	}
	
	@Override
	public List<IntervalSelection> getInterVals(int tierIx){
		TierAdapter<?> tierAdapter = tiers.get(tierIx);
		if(tierAdapter==null){
			return null;
		}
		return tierAdapter.getIntervals();
	}
	
	
//	@Override
//	public List<IntervalSelection> getInterVals(){
//		List<IntervalSelection> intervals = new ArrayList<IntervalSelection>();
//		for(int i = 0; i < tier.size();i++){
//			Interval<Boolean> inter = tier.getIntervalX(i);
//			if(inter!=null && inter.label!=null && inter.label){
//				Selection selection = new Selection();
//				selection.setSelectionStartSec(inter.startT);
//				selection.setSelectionEndSec(inter.endT);
//				
//				intervals.add(selection);
//			}
//		}
//		return intervals;
//	}
	
	
	private class Selection implements IntervalSelection {
		double startSampleIx = 0;
		double endSampleIx = 0;
		
//		String startLabel = "";
//		String endLabel = "";
		volatile private boolean isAdjusting = false; 

		
		@Override
		public void setSelectionStartPx(int px) {
			startSampleIx = viewStartSampleIx + ( viewSizeInSample * (px /(double) panelWidthInPx));
		}
		@Override
		public void setSelectionStartSec(double s){
			startSampleIx = s*hz;
		}
		@Override
		public void setSelectionEndPx(int px) {
			endSampleIx = viewStartSampleIx + (viewSizeInSample * (px /(double) panelWidthInPx));
		}
		public void setSelectionEndSec(double s){
			endSampleIx = s*hz;
		}

		@Override
		public int getSelectionStartPx(){
			return (int)Math.round(
					panelWidthInPx * (startSampleIx-viewStartSampleIx) / (double)viewSizeInSample
					);
		}
		public double getSelectionStartInSec(){
			return startSampleIx / hz;
		}
		@Override
		public int getSelectionEndPx() {
			return (int)Math.round(
					panelWidthInPx * (endSampleIx-viewStartSampleIx)/ (double)viewSizeInSample
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
				this.panelWidthInPx * (cursorPosInSample-this.viewStartSampleIx) / (double)this.viewSizeInSample
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
		cursorPosInSample = this.viewStartSampleIx + this.viewSizeInSample * (px /(double) this.panelWidthInPx);
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
		double [] minMaxCoordinates = new double [panelWidthInPx*2];
		
		double spanSize = viewSizeInSample / (double)panelWidthInPx;

		long t0 = System.currentTimeMillis();
		int sampleStart = viewStartSampleIx;
		int j = sampleStart;
		
		//-- vertical manipulation
		double multiplier = -1*this.panelHeightInPx/(double)(2*sampleHalfRange);
//		int shift = 1 * this.sampleHalfRange;
//		int shift = 1 * (sampleRangeUpper-sampleRangeLower);
		double shift = sampleMax;
//		double shift = 1.0d * this.sampleMin;
		
		for(int pixIx = 0; pixIx < panelWidthInPx ; pixIx++){ 
			//-- calc for each pixel: min & max sample values
			int jMax = sampleStart+(int)Math.round((pixIx+1)*spanSize);

			int spanMin = samples[j];
			int spanMax = samples[j];
			while(j < jMax && j < viewEndSampleIx){
				spanMin = samples[j] < spanMin ? samples[j] : spanMin; 
				spanMax = samples[j] > spanMax ? samples[j] : spanMax; 
				j++;
			}
			//-- transform values 
//			minMaxCoordinates[pixIx*2+0] = this.panelHeightInPx*(spanMin-this.sampleMin)/(sampleRange) ;
//			minMaxCoordinates[pixIx*2+1] = this.panelHeightInPx*(spanMax-this.sampleMin)/(sampleRange) ;
			
//			minMaxCoordinates[i*2+0] = this.panelHeightInPx*(spanMin+this.sampleHalfRange)/(double)(2*sampleHalfRange) ;
//			minMaxCoordinates[i*2+1] = this.panelHeightInPx*(spanMax+this.sampleHalfRange)/(double)(2*sampleHalfRange) ;
			
			minMaxCoordinates[pixIx*2+0] = multiplier*(spanMin-shift);
			minMaxCoordinates[pixIx*2+1] = multiplier*(spanMax-shift);
		}
		
		long t1 = System.currentTimeMillis();
		LOG.trace("Graph created in {} ms", t1-t0);
		return minMaxCoordinates;
	}

	
	@Override
	public void setSampleArray(int[] samples, int hz) {
		this.samples = samples;
		this.hz = hz;
		this.viewStartSampleIx = 0;
		this.viewEndSampleIx = this.samples.length;
		this.viewSizeInSample = this.viewEndSampleIx - this.viewStartSampleIx;
		
		sampleMin=sampleMax=samples[0];

		Map<Integer, Integer> sampleFreq = new TreeMap<Integer, Integer>();
		sampleFreqMax = -1;
		sampleFreqMaxVal = samples[0]; 
		
		for(int sample : samples){
			sampleSum += sample;
			sampleMax = (sample > sampleMax) ? sample : sampleMax;
			sampleMin = (sample < sampleMin) ? sample : sampleMin;
			
			Integer cnt = sampleFreq.get(sample);
			if(cnt==null){
				cnt =0;
			}else{
				cnt++;
			}
			if(cnt > sampleFreqMax){
				sampleFreqMax = cnt;
				sampleFreqMaxVal = sample;
			}
			sampleFreq.put(sample, cnt);
		}
		sampleRange = sampleMax-sampleMin;
		sampleMean = sampleSum/samples.length;
		
		sampleRangeUpper = (int) Math.abs(sampleMax - sampleFreqMaxVal);
		sampleRangeLower = (int) Math.abs(sampleMin - sampleFreqMaxVal);
		sampleHalfRange = sampleRangeUpper > sampleRangeLower ? sampleRangeUpper : sampleRangeLower;  
		
		LOG.trace("|-Sample max  : {}", (int) sampleMax);
		LOG.trace("|-Sample mean : {}", sampleMean);
		LOG.trace("|-Sample mode : {} ({})", (int) sampleFreqMaxVal, sampleFreqMax);
		LOG.trace("|-Sample min  : {}", (int) sampleMin);
		LOG.trace("|-Sample range: {}", (int)sampleRange );
		LOG.trace("|-       upper: {}", sampleRangeUpper );
		LOG.trace("|-       lower: {}", sampleRangeLower );
		LOG.trace("|-Half  range : {}", sampleHalfRange  );
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
		if(this.viewSizeInSample==this.panelWidthInPx){
			return px;
		}
//		final int visibleSampleN = this.viewEndSampleIx-this.viewStartSampleIx;
				
//		System.out.println(this.viewStartSampleIx + " - " + this.viewEndSampleIx + "( " +visibleSampleN +" )");
//		System.out.println(this.viewSize *  (px /(double) this.panelWidthInPx));
		int ret = (int)Math.round(
				this.viewSizeInSample *  (px /(double) this.panelWidthInPx));
//		System.out.println(" " + px + " > px > sample > " + ret);
		return ret;
	}


	public int getSampleIxFromSec(double sec){
		return (int)Math.round(sec*hz);
	}
	
	
	
	@Override
	public void zoomTo(double start, double end){
		//-- switch if necessary
		if(start>end){
			start = start+end;
			end   = start-end;
			start = start-end;
		}
		
		this.viewStartSampleIx = this.getSampleIxFromSec(start);
		this.viewEndSampleIx   = this.getSampleIxFromSec(end);
		//-- sanity check
		this.viewStartSampleIx = this.viewStartSampleIx < 0 ? 0 : this.viewStartSampleIx;
		this.viewEndSampleIx = this.viewEndSampleIx >= this.samples.length ? (this.samples.length-1) : this.viewEndSampleIx;
		
		viewSizeInSample = this.viewEndSampleIx - this.viewStartSampleIx;
	}

	@Override
	public void zoomOut() {
		this.viewStartSampleIx = 0;
		this.viewEndSampleIx   = (this.samples.length-1);
		this.viewSizeInSample  = this.samples.length;
	}
	
	
	@Override
	public double getSecFromPx(int px){
		final int visibleSampleN = (this.viewEndSampleIx-this.viewStartSampleIx);
		return (this.viewStartSampleIx + (visibleSampleN *  (px /(double) this.panelWidthInPx)))
				/ hz  // -- in seconds
				;
	}

	
	@Override
	public int getPxFromSampleIx(int x){
//		System.out.println(x + " > sample > px > " + this.panelWidthInPx * (x / (double)this.viewSize));
		return (int)Math.round(this.panelWidthInPx * x / (double)this.viewSizeInSample);
	}
	
	

	@Override
	public int addTier(IntervalTier<?> tier){
		if(tier instanceof BinaryTier){
			TierAdapter<Boolean> ta = new TierAdapter<Boolean>((BinaryTier)tier);
			tiers.put(tierN, ta);
			tierN++;
			editableTierIx = tierN-1; 
			return editableTierIx; //-- return Tier's ID 
		}
		if(tier instanceof VadErrorTier){
			TierAdapter<VadError> ta = new TierAdapter<VadError>((VadErrorTier)tier);
			tiers.put(tierN, ta);
			tierN++;
			return (tierN-1); //-- return Tier's ID 
		}
		return -1;
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
		
		TierAdapter<?> tierAdapter = tiers.get(editableTierIx);
		if(tierAdapter.tier instanceof BinaryTier){
			
			((BinaryTier)tierAdapter.tier).addInterval((Interval<Boolean>)inter);
		}else{
			LOG.error("Can't add interval to a non BinaryTier '" + tierAdapter.tier.getName() +"'!");
		}
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public void addInterval(Interval<?> interval) {
		if(interval.label instanceof Boolean){
			TierAdapter<?> tierAdapter = tiers.get(editableTierIx);
			if(tierAdapter.tier instanceof BinaryTier){
				System.out.println("Adding : " + interval);
				((BinaryTier)tierAdapter.tier).addInterval((Interval<Boolean>)interval);
				System.out.println(tierAdapter.tier);
			}else{
				LOG.error("Can't add interval to a non BinaryTier '" + tierAdapter.tier.getName() +"'!");
			}
		}
	}


	@Override
	public int getTierN() {
		return tierN;
	}


	@Override
	public Tier getTierByIx(int ix) {
		TierAdapter<?> tierAdapter = tiers.get(ix);
		if(tierAdapter==null){
			return null;
		}else{
			return tierAdapter.tier;
		}
	}


}
