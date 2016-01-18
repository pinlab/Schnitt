package info.pinlab.schnitt.gui;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.trs.Interval;
import info.pinlab.snd.trs.IntervalTier;
import info.pinlab.snd.trs.Tier;
import info.pinlab.snd.vad.BinaryHypoTier;
import info.pinlab.snd.vad.BinaryTargetTier;
import info.pinlab.snd.vad.VadError;
import info.pinlab.snd.vad.VadErrorTier;

public class WavGraphics implements WavPanelModel{
	public static Logger LOG = LoggerFactory.getLogger(WavGraphics.class);

	private double [] samples;
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
	private int sampleSum;
	private double sampleHalfRange, sampleRangeUpper, sampleRangeLower;
	int sampleFreqMax = 0;
	double sampleFreqMaxVal = 0; 


	//-- Tiers
	private int activeTierX = 0;
	private final IntervalSelection activeSelection;
	List<GuiAdapterForTier<?>> tiers = new ArrayList<GuiAdapterForTier<?>>();

	//-- SELECTION Graphics
	static int marginFromTop = 20; 
	static int defaultSelectionHeight = 50;
	static int defaultSelectionMarginTop = 20;

	static int[][] defaultFillColorsInRgb = new int[][]{
		{204, 215, 119},
		{100, 141, 214},
		{214, 100, 100},
	};


	int tierN = 0; 

	VadErrTierAdapter vadTierAdapter  = null;
	BinaryTierAdapter hypoTierAdapter = null;



	abstract class AbstractTierAdapter<T> implements GuiAdapterForTier<T>{
		final Class<T> clazz;
		private final IntervalTier<T> tier;
		boolean isVisible = true;
		boolean isActive  = false;
		boolean isEditable= false;

		String label;

		//-- 
		private int selectionYTop ;//= 50; /*px*/
		private int selectionYBottom    ;// = 40; /*px*/

		//		private int [] selColInRgb = new int []{214, 255, 161}; // greenish;
		private int [] fillColorInRgb   = new int []{204, 215, 119};  //-- grayish

		List<IntervalSelection> selections = new ArrayList<IntervalSelection>();


		AbstractTierAdapter(IntervalTier<T> tier, Class<T> cls){
			this.tier = tier;
			this.clazz = cls;

			int padding = 2;

			int ix = -1;

			if(tier instanceof BinaryTargetTier){
				ix = 1;
			}else if(tier instanceof BinaryHypoTier){
				ix = 0;
			}else if(tier instanceof VadErrorTier){
				ix = 2;
			}else{
				throw new IllegalStateException("Don't know what to do with this tier '" + tier.getName() +"'");
			}

			fillColorInRgb = defaultFillColorsInRgb[ix];
			//			System.out.println(ix);
			//			System.out.println(defaultSelectionHeight);
			selectionYTop = marginFromTop + ix*defaultSelectionHeight + ix*padding;
			selectionYBottom = selectionYTop+defaultSelectionHeight;


			LOG.info("Adding '" + tier.getName() + "' tier named '"+ tier.getName()+"'" + " to Y " + selectionYTop + "-" + selectionYBottom);
			//-- shifting down tiers, one by one
			//			selectionYTop = marginFromTop + tierN*defaultSelectionHeight + tierN*padding;
			//			selectionYBottom = selectionYTop+defaultSelectionHeight;
			//			marginFromTop = selectionMarginTop+ + selectionHeight;
			//			System.out.println(selectionMarginTop);
			tierN++;
			tiers.add(this);
			refreshSelection();
		}


		synchronized public IntervalSelection getSelectionForX(int x){
			this.refreshSelection();
			for(int i = 0; i < selections.size();i++){
				IntervalSelection sel = selections.get(i);
				if(x >= sel.getSelectionStartPx() && x <= sel.getSelectionEndPx()){
					return sel;
				}
			}
			return null;
		}

		synchronized List<IntervalSelection> getIntervals(){
			return selections;
		}

		@Override
		synchronized public int getSelectionN(){
			return selections.size();
		}

		@Override
		synchronized public IntervalSelection getSelectionX(int ix){
			return selections.get(ix);
		}
		@Override
		public IntervalTier<T> getTier(){
			return this.tier;
		}

		@Override
		public boolean isVisible() {		return isVisible; 	}
		@Override
		public boolean isActive() {			return isActive;	}
		@Override
		public boolean isEditable(){		return isEditable;	}
		@Override
		public void isVisible(boolean b) {	isVisible(b);		}
		@Override
		public int getSelectionYTop(){	return selectionYTop;	}
		@Override
		public int getSelectionYBottom() {	return selectionYBottom;		}
		@Override
		public int[] getSelectionFillColorInRgb(){return fillColorInRgb;	}

		@Override
		public Class<T> getTierType(){
			return clazz;
		}
	}



	class BinaryTierAdapter extends AbstractTierAdapter<Boolean> implements GuiAdapterForBinaryTier{
		BinaryTierAdapter(IntervalTier<Boolean> t){
			super(t, Boolean.class);
		}
		public void refreshSelection(){
			selections.clear();
			for(int i = 0; i < super.tier.size();i++){
				Interval<Boolean> inter = super.tier.getIntervalX(i);
				if(inter!=null && inter.label!=null && inter.label){
					Selection selection = new Selection();
					selection.setSelectionStartSec(inter.startT);
					selection.setSelectionEndSec(inter.endT);

					selections.add(selection);
				}
			}
		}
		@Override
		synchronized public void addInterval(Interval<Boolean> interval){
			if(super.tier.isEditable()){
				super.tier.addInterval(interval);
				refreshSelection();
			}else{
				LOG.error("Boolean tier is not updatable '" + super.tier.getName() + "'");
			}
		}
	}


	class VadErrTierAdapter extends AbstractTierAdapter<VadError> implements GuiAdapterForVadErrTier{
		VadErrTierAdapter(IntervalTier<VadError> t){
			super(t, VadError.class);
		}

		public void refreshSelection(){
			((VadErrorTier) super.tier).refresh();
			selections.clear();
			for(int i = 0; i < super.tier.size();i++){
				Interval<VadError> inter = super.tier.getIntervalX(i);
				if(inter!=null){
					Selection selection = new Selection(inter.label.name());
					selection.setSelectionStartSec(inter.startT);
					selection.setSelectionEndSec(inter.endT);
					selections.add(selection);
				}
			}
		}
	}

	public class Selection implements IntervalSelection {
		double startSampleIx = 0;
		double endSampleIx = 0;

		String label = "";

		public Selection(){};

		public Selection(String label){
			this.label = label;
		}

		public String getLabel(){
			return label;
		}

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


	public WavGraphics(){
		activeSelection = new Selection();

		BinaryHypoTier hypo = new BinaryHypoTier();
		hypoTierAdapter = (BinaryTierAdapter)addTier(hypo); 
		hypoTierAdapter.isEditable = true;
		hypoTierAdapter.isActive = true;
	}


	public IntervalSelection getSelectionAt(int x, int y){

		return null;
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
	synchronized public double[] getWaveCurvePointsCondensed(){
		double [] minMaxCoordinates = new double [panelWidthInPx*2];

		double spanSize = viewSizeInSample / (double)panelWidthInPx;

		long t0 = System.currentTimeMillis();
		int sampleStart = viewStartSampleIx;
		int j = sampleStart;

		//-- vertical manipulation
//		double multiplier = -1*this.panelHeightInPx/(double)(2*sampleHalfRange);
		double multiplier =  -1*this.panelHeightInPx/(double)(2*(sampleHalfRange-sampleMean));
		//		int shift = 1 * this.sampleHalfRange;
		//		int shift = 1 * (sampleRangeUpper-sampleRangeLower);
		double shift = sampleHalfRange;
//		double shift = sampleMax;
		//		double shift = 1.0d * this.sampleMin;

		for(int pixIx = 0; pixIx < panelWidthInPx ; pixIx++){ 
			//-- calc for each pixel: min & max sample values
			int jMax = sampleStart+(int)Math.round((pixIx+1)*spanSize);

			double spanMin = samples[j];
			double spanMax = samples[j];
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
	synchronized public void setWav(WavClip wav){
		if(wav==null){
			throw new IllegalArgumentException("WavClip can't be null!");
		}
//		this.setSampleArray(wav.toIntArray(), (int)wav.getAudioFormat().getSampleRate());
		this.setSampleArray(wav.toDoubleArray(), (int)wav.getAudioFormat().getSampleRate());
	}


	synchronized public void setSampleArray(double[] sampls, int hz) {
		if(sampls.length==0){
			throw new IllegalArgumentException("Sample array length is zero!");
		}
		this.hz = hz;
		
		this.samples = Arrays.copyOfRange(sampls, 0, sampls.length);
		sampleMax = samples[0];
		sampleMin = samples[0];
		sampleMean = 0;
		sampleSum = 0;
		
		for(int i = 0; i < this.samples.length ; i++){
			sampleSum += samples[i];
			sampleMax = (samples[i] > sampleMax) ? samples[i] : sampleMax;
			sampleMin = (samples[i] < sampleMin) ? samples[i] : sampleMin;
		}

		this.viewStartSampleIx = 0;
		this.viewEndSampleIx = this.samples.length;
		this.viewSizeInSample = this.viewEndSampleIx - this.viewStartSampleIx;
		sampleRange = sampleMax-sampleMin;
		sampleMean = sampleSum/samples.length;

		sampleRangeUpper = Math.abs(sampleMax - sampleMean /*sampleFreqMaxVal*/);
		sampleRangeLower = Math.abs(sampleMin - sampleMean /*sampleFreqMaxVal*/);
		sampleHalfRange = sampleRangeUpper > sampleRangeLower ? sampleRangeUpper : sampleRangeLower;  


		
		@SuppressWarnings("unchecked")
		IntervalTier<Boolean> tier = (IntervalTier<Boolean>)tiers.get(0).getTier();
		tier.addInterval(0.0d, this.samples.length / (double)hz, false);

		LOG.trace("|-Sample max  : {}", sampleMax);
		LOG.trace("|-Sample mean : {}", sampleMean);
		LOG.trace("|-Sample mode : {} ({})",  sampleFreqMaxVal, sampleFreqMax);
		LOG.trace("|-Sample min  : {}", sampleMin);
		LOG.trace("|-Sample range: {}", sampleRange );
		LOG.trace("|-       upper: {}", sampleRangeUpper );
		LOG.trace("|-       lower: {}", sampleRangeLower );
		LOG.trace("|-Half  range : {}", sampleHalfRange  );
		
	}
	
	@Override
	synchronized public void setSampleArray(int[] sampls, int hz){
		this.samples = new double[sampls.length];
		for(int i = 0 ;i < sampls.length ;i++){
			this.samples[i] = sampls[i];
		}
//		this.samples = sampls;
		this.hz = hz;
		this.viewStartSampleIx = 0;
		this.viewEndSampleIx = this.samples.length;
		this.viewSizeInSample = this.viewEndSampleIx - this.viewStartSampleIx;

		sampleMin=sampleMax=this.samples[0];

//		Map<Double, Integer> sampleFreq = new TreeMap<Integer, Integer>();
		sampleFreqMax = -1;
		sampleFreqMaxVal = sampls[0]; 

		for(double sample : sampls){
			sampleSum += sample;
			sampleMax = (sample > sampleMax) ? sample : sampleMax;
			sampleMin = (sample < sampleMin) ? sample : sampleMin;

//			Integer cnt = sampleFreq.get(sample);
//			if(cnt==null){
//				cnt =0;
//			}else{
//				cnt++;
//			}
//			if(cnt > sampleFreqMax){
//				sampleFreqMax = cnt;
//				sampleFreqMaxVal = sample;
//			}
//			sampleFreq.put(sample, cnt);
		}
		sampleRange = sampleMax-sampleMin;
		sampleMean = sampleSum/samples.length;

		sampleRangeUpper = (int) Math.abs(sampleMax - sampleMean /*sampleFreqMaxVal*/);
		sampleRangeLower = (int) Math.abs(sampleMin - sampleMean /*sampleFreqMaxVal*/);
		sampleHalfRange = sampleRangeUpper > sampleRangeLower ? sampleRangeUpper : sampleRangeLower;  

		@SuppressWarnings("unchecked")
		IntervalTier<Boolean> tier = (IntervalTier<Boolean>)tiers.get(0).getTier();
		tier.addInterval(0.0d, this.samples.length / (double)hz, false);

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


	private GuiAdapterForTier<Boolean> addActiveTier(IntervalTier<Boolean> tier){
		if(tiers.size()>0){
			tiers.remove(0);
		}
		BinaryTierAdapter ta = new BinaryTierAdapter(tier);
		ta.isActive = true;
		ta.isEditable = true;
		tiers.add(0, ta);
		return ta;
	}



	public GuiAdapterForTier<VadError> addTier(VadErrorTier err){
		vadTierAdapter =  new VadErrTierAdapter((VadErrorTier)err);;
		return vadTierAdapter; 
	}
	public GuiAdapterForTier<Boolean> addTier(BinaryTargetTier targ){
		GuiAdapterForTier<Boolean> ta = new BinaryTierAdapter((IntervalTier<Boolean>)targ);
		return ta;
	}
	public GuiAdapterForTier<Boolean> addTier(BinaryHypoTier hypo){
		GuiAdapterForTier<Boolean> ta = addActiveTier((IntervalTier<Boolean>)hypo);
		return ta;
	}


	@Override
	public IntervalSelection getActiveIntervalSelection() {
		return activeSelection;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public  void addIntervalToActiveTier(Interval interval) {
		if(interval==null){
			LOG.error("Wrong argument: null Interval");
			return;
		}
		GuiAdapterForTier tierAdapter = tiers.get(activeTierX);
		if(tierAdapter.isEditable()){
			if(Boolean.class.equals(tierAdapter.getTierType())){ //-- if binary tier
				GuiAdapterForBinaryTier binaryTierAdapter = (GuiAdapterForBinaryTier) tierAdapter;
				//-- check if interval's TYPE PARAM == tier's TYPE PARAM 
				binaryTierAdapter.addInterval(interval);

				if(vadTierAdapter!=null){
					vadTierAdapter.refreshSelection();
				}
			}
		}else{
			LOG.trace("Tier '"+ activeTierX + " is not editable!");
		}
	}


	@Override
	public int getTierN() {
		return tiers.size();
	}


	@Override
	public Tier getTierByIx(int ix) {
		GuiAdapterForTier<?> tierAdapter = tiers.get(ix);
		if(tierAdapter==null){
			return null;
		}else{
			return tierAdapter.getTier();
		}
	}


	@Override
	public int getTierAdapterN() {
		return tiers.size();
	}



	@Override
	public GuiAdapterForTier<?> getTierAdapter(int ix) {
		if(ix > tiers.size()){
			LOG.error("Out of bound tier ix '" + ix + "' >= " + tiers.size());
			return null;
		}
		return tiers.get(ix);
	}


}
