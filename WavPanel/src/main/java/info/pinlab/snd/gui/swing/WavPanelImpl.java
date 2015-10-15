package info.pinlab.snd.gui.swing;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.WavUtil;
import info.pinlab.snd.gui.WavPanelUI;
import info.pinlab.snd.trs.Tier;


/**
 * Swing's JPanel implementation of WavPanel. 
 * 
 * @author kinoko
 *
 */
public class WavPanelImpl extends JPanel 
			implements  WavPanelUI
			, MouseListener, MouseMotionListener,
			ComponentListener
{
	private static final long serialVersionUID = -1689117249151777252L;
	
	private static Logger logger = LogManager.getLogger(WavPanelImpl.class);
	
	private int [] samples = null;
//	private int hz = 0;
	
	int viewStartSampleX = 0;
	int viewEndSampleX = 0; 
	double hz = 0.0d;
	int sampleYmax = 0;
	int sampleYmin = 0;
	int sampleN = 0;
	GeneralPath polyline = null;
	float labelAlpha = 0.4f; //-- the small the fade 
	double zoomY = 0.7; 
	double zoomX = 1.0;

	int cursorPosInFrame = 0;
	int cursorPosInPx = 0;
	
	SelectionBuilder selectionBuilder = new SelectionBuilder();
//	List<Selection> selections = new ArrayList<Selection>();
	Selection currentSelection = null; 
	
	//-- COLORS --//
	private Color bgCol = Color.WHITE; //new Color(243,243,248); //Color.BLACK;
	private Color fontCol = new Color(126, 179, 102);
	private Color labelBgCol = new Color(204, 215, 119);
	private Color cursorCol = new Color(126, 179, 102); //new Color(119, 127, 0);//Color.white;
	static private Color lineCol = new Color(100,100,220);;
	private Color selCol = new Color(214, 255, 161); // greenish;
	
	public class Selection{
		public final int id;
		public final int startFrameX ;
		public final int endFrameX ;
		boolean isLeftAxes = true;

		public final int startInMs ;
		public final int endInMs ;
		public final int durInMs;

//		private final WavClip wav;
		public Selection(int startX, int endX, int id){
			startFrameX = startX;
			endFrameX = endX;
//			float hz = wav.getAudioFormat().getSampleRate();
			startInMs = fromFrameToMs(startFrameX);
			endInMs = fromFrameToMs(endFrameX);
			durInMs =  endInMs - startInMs;
//			startInMs =  (int)Math.ceil(1000 * startInFrame / hz);
//			endInMs = (int)Math.ceil(1000 * endInFrame / hz);
//			durInMs = (int)Math.ceil(1000 * (endInFrame-startInFrame) / hz);
//			this.wav = wav;
			this.id = id;
		}
//		public WavClip getWav(){
//			return wav;
//		}
	}
	
	private class SelectionBuilder{
		private int selStartSampleX = -1;
		private int selEndSampleX = -1; 
		private int axisSampleX = -1;
		private int idCnt=0;
		boolean isAdjusting = false;
		
		String leftLabel = "";
		String rightLabel = "";
		String durLabel = "";
		
		
		private void clear(){
			selStartSampleX = axisSampleX = selEndSampleX = 0;
			leftLabel = rightLabel = durLabel = "";
		}
		
		
		
		public void setSelectionAxis(int frameX){
			axisSampleX = frameX;
			selStartSampleX = frameX;
		}
		
		public void setSelectionMovingEdge(int frameX){
			selStartSampleX =  frameX < axisSampleX ? frameX : axisSampleX  ;  
			selEndSampleX = frameX > axisSampleX ? frameX : axisSampleX ; 
			_doLabel();
		}
		private void _doLabel(){
			int left = (int)Math.floor(1000.0d * selStartSampleX / hz);
			int right = (int)Math.floor(1000.0d * selEndSampleX / hz);
			int dur = right-left;
			leftLabel = String.format("%d", left); 
			rightLabel = String.format("%d", right); 
			durLabel = String.format("%d", dur);
		}
		public void increaseSelection(int frameX){
			if(axisSampleX==selStartSampleX){
				setSelectionMovingEdge(selEndSampleX += frameX); 
			}else{
				setSelectionMovingEdge(selStartSampleX += frameX); 
			}
			_doLabel();
		}
		
		private Selection build(){
			Selection sel = null;
			if(selEndSampleX-selStartSampleX > 5){
				sel = new Selection(selStartSampleX, selEndSampleX, idCnt++);
				if(axisSampleX == selEndSampleX)
					sel.isLeftAxes = false;
			}
			clear();
			return sel;
		}
	}
	
	
	
	
	
	
	
	
	
	public WavPanelImpl(){
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addComponentListener(this);

		
		this.setOpaque(true);
		this.setBackground(bgCol);
	}


	
//	@Override
	public void mousePressed(MouseEvent e) {
		final int x = e.getX();
		final int f = fromPixToFrame(x);
		selectionBuilder.isAdjusting = true;
		
		if(currentSelection != null){
			int diffX = x-fromMsToPix(currentSelection.startInMs);
			if((selMagnetDist > diffX) && (diffX > -selMagnetDist )){
//				System.out.println("Magnet ");
				selectionBuilder.setSelectionAxis(currentSelection.endFrameX);
				selectionBuilder.setSelectionMovingEdge(f);
				currentSelection = null;
				return;
			}
		}
		currentSelection = null;
		selectionBuilder.clear();
		selectionBuilder.setSelectionAxis(f);
		selectionBuilder.setSelectionMovingEdge(f);
		cursorPosInFrame = f;
		repaint();
	}
//	@Override
	public void mouseReleased(MouseEvent e) {
//		System.out.println("MOUSE RELEASED!");
		int x = fromPixToFrame(e.getX());
		cursorPosInFrame = x;
    	selectionBuilder.setSelectionMovingEdge(x);
    	selectionBuilder.isAdjusting = false;
		currentSelection = selectionBuilder.build();
		repaint();
	}


	public void mouseMoved(MouseEvent arg0) {
	}
	public void mouseClicked(MouseEvent e) {
	}

//	@Override
	public void mouseDragged(MouseEvent e) {
//	System.out.println("Dragged " + e.getX());
		cursorPosInFrame = fromPixToFrame(e.getX());
		selectionBuilder.setSelectionMovingEdge(cursorPosInFrame);
//		if(selectionBuilder.isAdjusting){
//			int f = fromPixToFrame(e.getX());
//		}else{
//			selectionBuilder.setSelectionEndFrame(fromPixToFrame(e.getX()));
//		}
    	repaint();
	}
	
	
	
	public void setTier(Tier tier) {
	}
	public void setSampleArray(int [] samples, int hz) {
		this.samples = samples;
		this.hz = hz;
		
		
		this.repaint();
	}

	
	
	
	int selMagnetDist = 10 /*pixels*/;
	

	
	
	
	private GeneralPath calcPolyLine(){
		if (samples==null){
			return null;
		}
		int pixelN = this.getWidth();
		int pixelH = this.getHeight();
		
		GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO) ;

		if(samples.length > pixelN){ //-- more pixels than samples
			double spanSize = samples.length / (double)pixelN;
			System.out.println("Pixel #=" + pixelN);
			
			int sampleMin = samples[0]; 
			int sampleMax = samples[0];
			int [] spanMins = new int[pixelN]; 
			int [] spanMaxs = new int[pixelN]; 
			
			
			int j = 0;
			for(int i = 0; i < pixelN ; i++){ //-- let's calc every pixel!
				int jMax = (int)Math.round((i+1)*spanSize);
//				System.out.println("  " + j +  "..." + jMax);

				int spanMin = samples[j];
				int spanMax = samples[j];
				while(j < jMax && j < samples.length){
					spanMin = samples[j] < spanMin ? samples[j] : spanMin; 
					spanMax = samples[j] > spanMax ? samples[j] : spanMax; 
					j++;
				}
//				j--;
				spanMins[i] = spanMin;
				spanMaxs[i] = spanMax;
				sampleMin = (spanMin < sampleMin) ? spanMin : sampleMin;
				sampleMax = (spanMax > sampleMax) ? spanMax : sampleMax;
			}
			
//			sampleMin = (-sampleMax < sampleMin)? -sampleMax : sampleMin;
//			sampleMax = (-sampleMin > sampleMax)? -sampleMin : sampleMax;
//			sampleMax *= 1.05; //-- give some padding
//			sampleMin *= 1.05; //-- give some padding 
			
			
			double range = (sampleMax - sampleMin)/pixelH;
			path.moveTo(0, spanMins[0]);
			for(int i = 0 ; i < pixelN ; i++){
				spanMaxs[i] =(int)Math.ceil( (spanMaxs[i]-sampleMin)/range) ; 
				spanMins[i] =(int)Math.floor((spanMins[i]-sampleMin)/range);
				path.lineTo(i, spanMaxs[i]);
				path.lineTo(i, spanMins[i]);
			}
		}else{ //-- more pixels than samples -> interpolate
			double spanSize = pixelN/(double)samples.length; 
			int sampleMax = samples[0];
			int sampleMin = samples[0];
			for (int i = 0; i < samples.length ; i++){
				sampleMax = (samples[i] > sampleMax) ? samples[i] : sampleMax;
				sampleMin = (samples[i] < sampleMin) ? samples[i] : sampleMin;
			}
			
			double range = 1.05*(sampleMax - sampleMin)/pixelH;
			path.moveTo(0, samples[0]);
			for (int i = 0; i < samples.length ; i++){
				path.lineTo(i*spanSize, (samples[i]-sampleMin)/range);
			}
			
		}
		return path;
//		System.out.println(this.getWidth() +"\t" + samples.length);
		
	}
	
	
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		GeneralPath path = calcPolyLine();
		int pixelH = this.getHeight();
		

		
		if(path!=null){
//			Graphics2D g2 = (Grahpics2D)g;
			Graphics2D g2 = (Graphics2D) g.create(); 
			g2.setColor(lineCol);
			g2.draw(path);
			g2.dispose();
		}

		//-- paint cursor --//
		int cursorX = pixelH /2;
		g.setColor(cursorCol);
//		final int f = cursorPosInFrame;
//		final int cursorX = (int)Math.floor(w * (f-viewStartSampleX) / (double)(viewEndSampleX-viewStartSampleX)); 
		g.drawLine(cursorX, 0, cursorX, pixelH);
//		g.setFont(timeLabelFont);
//		g.drawString(String.format("%d", fromFrameToMs(f)), cursorX + 2, h-1);
		
	}
	
	


	private int fromPixToFrame(int px){
		return viewStartSampleX + (int)Math.floor((viewEndSampleX-viewStartSampleX)*(px /(double)this.getWidth()));
	}
	private int fromPixToMs(int px){
		double frame = viewStartSampleX + (viewEndSampleX-viewStartSampleX)*(px /(double)this.getWidth());
		return (int)Math.floor(1000*frame / hz);
	}
	private int fromMsToFrame(long ms){
		return (int)Math.floor(ms*hz/1000.0d);
	}
	private int fromMsToPix(long ms){
		return 0;
//		return (int)Math.ceil(((double)ms * (double)this.getWidth())/ (double) rootWav.getDurInMs()); 
	}
	int fromFrameToMs(long f){
		return  (int)Math.floor((f*1000.d)/hz);
	}
	

	
	
	
	public void addTier(Tier tier) {
	}


	
	public static void main(String[] args) throws Exception{
		WavPanelImpl panel = new WavPanelImpl();
		
		WavClip wav = new WavClip(panel.getClass().getResourceAsStream("sample.wav"));
		int [] samples = WavUtil.getIntArray(wav);
		panel.setSampleArray(samples, 16000);
		
		
		JFrame frame = new JFrame("WavPanel test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setSize(800, 400);
//		frame.pack();
		frame.setVisible(true);
		
		logger.info("What'S up?" );
		logger.error("Error? What'S up?" );
		logger.fatal("FATAL ? "+ logger.getLevel());
	}






	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}






	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}






	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}






	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
















	
	
	
}
