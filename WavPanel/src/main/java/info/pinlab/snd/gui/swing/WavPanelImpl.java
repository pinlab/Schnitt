package info.pinlab.snd.gui.swing;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.gui.IntervalSelection;
import info.pinlab.snd.gui.WavGraphics;
import info.pinlab.snd.gui.WavPanelModel;
import info.pinlab.snd.gui.WavPanelUI;


/**
 * Swing's JPanel implementation of WavPanel. 
 * 
 * @author kinoko
 *
 */
public class WavPanelImpl extends JPanel 
			implements  WavPanelUI, ComponentListener
			, MouseListener, MouseMotionListener
			, KeyListener
{
	private static final long serialVersionUID = -1689117249151777252L;
	private static Logger LOG = LoggerFactory.getLogger(WavPanelImpl.class);

	private WavPanelModel model = null;
	
	private static Font timeLabelFont = new Font("Arial", Font.PLAIN, 9);
	Font labelFont = new Font("Helvetica", Font.PLAIN, 1);
	
	
//	int selectionMinSizeInPx = 80;
	
	
	int viewStartSampleX = 0;
	int viewEndSampleX = 0; 
//	double hz = 0.0d;
	int sampleYmax = 0;
	int sampleYmin = 0;
	int sampleN = 0;
	GeneralPath polyline = null;
	float labelAlpha = 0.4f; //-- the small the fade 
	double zoomY = 0.7; 
	double zoomX = 1.0;

	
	//-- COLORS in RGB --//
	private Color bgCol = Color.WHITE; //new Color(243,243,248); //Color.BLACK;
	private Color fontCol = new Color(126, 179, 102);
	private Color labelBgCol = new Color(204, 215, 119);
	private Color cursorCol = new Color(126, 179, 102); //new Color(119, 127, 0);//Color.white;
	static private Color lineCol = new Color(100,100,220);;
	private Color selCol = new Color(214, 255, 161); // greenish;
	private Color selBorderLineCol = new Color(100, 100, 100); // gray
	
	
	
	public WavPanelImpl(){
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addComponentListener(this);
		
		this.addKeyListener(this);
		this.setFocusable(true);
		
		this.setOpaque(true);
		this.setBackground(bgCol);
	}



	@Override
	public void setWavPanelModel(WavPanelModel model) {
		this.model = model;
	}


	
	@Override
	public void mousePressed(MouseEvent e) {
		final int x = e.getX();
		model.setCursorPosToPx(x);
//		model.getActiveIntervalSelection().clear();
		
		model.getActiveIntervalSelection().setSelectionStartPx(x);
		model.getActiveIntervalSelection().setSelectionEndPx(x);
		model.getActiveIntervalSelection().isAdjusting(true);
		
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		model.setCursorPosToPx(e.getX());
    	
		model.getActiveIntervalSelection().isAdjusting(false);
		model.getActiveIntervalSelection().setSelectionEndPx(x);
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int endPx = e.getX();
		model.setCursorPosToPx(endPx);
		model.getActiveIntervalSelection().setSelectionEndPx(endPx);
    	repaint();
	}
	
	public void mouseMoved(MouseEvent e){
		System.out.println(e.getX());
	}
	public void mouseClicked(MouseEvent e){	}
	
	
	private GeneralPath calcPolyLine2(){
		GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO) ;
		
		model.setViewWidthInPx(this.getWidth());
		model.setViewHeightInPx(this.getHeight());


		double [] minMaxs = model.getWaveCurvePointsCondensed();
		path.moveTo(0, minMaxs[0]);
		for(int i = 0 ; i < (minMaxs.length/2) ; i++){
//			System.out.println(minMaxs[i*2]);
			path.lineTo(i, minMaxs[i*2+0]);
			path.lineTo(i, minMaxs[i*2+1]);
		}
		return path;
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited( MouseEvent ignored) {}

	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		GeneralPath path = calcPolyLine2();
		int pixelH = this.getHeight();
		
		
		if(path!=null){
			Graphics2D g2 = (Graphics2D) g.create(); 
			g2.setColor(lineCol);
			g2.draw(path);
			g2.dispose();
			
			
		}

		
		//-- ACTIVE SELECTION --//
		IntervalSelection selection = model.getActiveIntervalSelection();
//		System.out.println(selection.getSelectionDurInSec());
        Graphics2D g2 = (Graphics2D) g.create(); 
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, labelAlpha)); 

		int left  = selection.getSelectionStartPx();
		//-- right is moving left is the axis
		int right = selection.getSelectionEndPx();
		int axis = left;
		double axisT = selection.getSelectionStartInSec();
		if(left>right){ //-- swap
			left  = left + right;
			right = left - right;
			left  = left - right;
		}else{
			
		}
	
		g2.setColor(selCol);
		g2.fillRect(left, 0, right-left, pixelH);
		g2.setColor(selBorderLineCol);
		g2.drawLine(left, 0, left, pixelH);
		g2.drawLine(right, 0, right, pixelH);
		
		g2.setFont(timeLabelFont);
		g2.drawString(String.format("%.3f", axisT), axis + 2, pixelH-1);

		//-- NON-ACTIVE SELECTIONS -> LABELS  --//
		for(IntervalSelection interval : model.getInterVals()){
			left = interval.getSelectionStartPx();
			right = interval.getSelectionEndPx();

			g2.setColor(labelBgCol);
			g2.fillRect(left, 40, right-left, pixelH-80);
			g2.setColor(selBorderLineCol);
			g2.drawLine(left, 40, left, pixelH-80);
			g2.drawLine(right, 40, right, pixelH-80);
		}
		
		
//		int labW = right-left; 
//		int fontH = 0;
//		int fontW = 0;
//		g2.setFont(labelFont);
//		do{
//			Font font = g2.getFont();
//			g2.setFont(font.deriveFont( font.getSize() + 0.5f) );
//			
//			FontMetrics metrics = g2.getFontMetrics();
//			fontH = metrics.getDescent() + metrics.getAscent();
//			fontW = metrics.stringWidth(s+" ");
////		System.out.println(fontW + " " + fontH);
//		}while(fontH < fontHeight &&  fontW < labW);
//		yPad = g2.getFontMetrics().getDescent() > ((boxHeight - fontH)/2)  ? g.getFontMetrics().getDescent()+labelMargin : ((boxHeight - fontH)/2) + labelMargin;
//
//			
		
		
		//-- CURSOR --//
		final int cursorX = model.getCursorPositionInPx();
		g.setColor(cursorCol);
		g.drawLine(cursorX, 0, cursorX, pixelH);
		
		g.setFont(timeLabelFont);
		g.drawString(String.format("%.3f", model.getCursorPositionInSec()), cursorX + 2, pixelH-1);
		
	}
	



	public void componentResized(ComponentEvent arg0) {
		model.setViewWidthInPx(this.getWidth());
		model.setViewHeightInPx(this.getHeight());
	}
	public void componentHidden(ComponentEvent ignore) {}
	public void componentMoved(ComponentEvent ignore){}
	public void componentShown(ComponentEvent ignore) {}
	
	
	



	public static void main(String[] args) throws Exception{
		WavClip wav = new WavClip(WavPanelImpl.class.getResourceAsStream("sample.wav"));
		
		WavPanelModel model = new WavGraphics();
//		int [] samples = WavUtil.getIntArray(wav);
		model.setSampleArray(wav.toIntArray(), (int)wav.getAudioFormat().getSampleRate());
		
		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);	
		
		JFrame frame = new JFrame("WavPanel test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setSize(800, 400);
//		frame.pack();
		frame.setVisible(true);
		
		
		LOG.trace("Trace");
		LOG.info("What'S up?" );
		LOG.warn("Warning");
		LOG.error("Error? What'S up?" );
		LOG.error("FATAL ? "+ LOG.getName());
	}



	@Override
	public void keyPressed(KeyEvent e) {
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
		if(ks.getKeyCode() == e.getKeyCode()){
			
			model.addIntervalSelection(model.getActiveIntervalSelection());
//			model.getActiveIntervalSelection().clear();
			this.repaint();
		}
	}



	@Override
	public void keyReleased(KeyEvent arg0) {
	}



	@Override
	public void keyTyped(KeyEvent e) {
	}










	
	
	
}
