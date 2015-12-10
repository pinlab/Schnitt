package info.pinlab.schnitt.gui.swing;


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
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.schnitt.gui.GuiAdapterForTier;
import info.pinlab.schnitt.gui.IntervalSelection;
import info.pinlab.schnitt.gui.WavPanelModel;
import info.pinlab.schnitt.gui.WavPanelUI;
import info.pinlab.snd.trs.Interval;
import info.pinlab.snd.trs.VadErrorTier;
import info.pinlab.snd.vad.VadError;


/**
 * Swing's JPanel implementation of WavPanel. 
 * 
 * 
 * <pre>
 * {@code 
		WavPanelModel model = new WavGraphics();

  		WavClip wav = new WavClip(WavPanelImpl.class.getResourceAsStream("sample.wav"));
		model.setSampleArray(
				wav.toIntArray(), 
				,(int)wav.getAudioFormat().getSampleRate());

		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);	

		JFrame frame = new JFrame("WavPanel test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setSize(800, 400);
		frame.setVisible(true);
		}
 * </pre>									
 * 
 * @author Gabor Pinter
 *
 */
public class WavPanelImpl extends JPanel 
implements  WavPanelUI, ComponentListener
, MouseListener, MouseMotionListener
, KeyListener
{
	private static final long serialVersionUID = -1689117249151777252L;
	public static Logger LOG = LoggerFactory.getLogger(WavPanelImpl.class);

	private WavPanelModel model = null;

	private static Font timeLabelFont = new Font("Arial", Font.PLAIN, 9);
	private static Font errLabelFont = new JTextField().getFont().deriveFont(Font.BOLD, 24); 

	Font labelFont = new Font("Helvetica", Font.PLAIN, 1);


	int viewStartSampleX = 0;
	int viewEndSampleX = 0; 
	//	double hz = 0.0d;
	int sampleYmax = 0;
	int sampleYmin = 0;
	int sampleN = 0;
	GeneralPath polyline = null;
	float alphaForActiveLabel = 0.4f;   //-- the smaller the fader 
	float alphaForPassiveLabel = 0.25f; //-- the smaller the fader 
	double zoomY = 0.7; 
	double zoomX = 1.0;


	//-- COLORS in RGB --//
	private Color bgCol = Color.WHITE; //new Color(243,243,248); //Color.BLACK;
	//	private Color fontCol = new Color(126, 179, 102);
	//	private Color bgColForActiveLabel = new Color(255, 59, 0); //-- redish
	//	private Color bgColForPassiveLabel = new Color(204, 215, 119);  //-- grayish 


	private Color cursorCol = new Color(126, 179, 102); //new Color(119, 127, 0);//Color.white;
	static private Color lineCol = new Color(100,100,220);;
	private Color selCol = new Color(214, 255, 161); // greenish;
	private Color selBorderLineCol = new Color(100, 100, 100); // gray

	GeneralPath path = null;

	private final Map<Integer, Runnable> shortCutMap ;


	public WavPanelImpl(){
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addComponentListener(this);

		this.addKeyListener(this);
		this.setFocusable(true);

		this.setOpaque(true);
		this.setBackground(bgCol);

		shortCutMap = new HashMap<Integer, Runnable>();

		//		  System.setProperty("awt.useSystemAAFontSettings","on");
		//		  System.setProperty("swing.aatext", "true");

		//-- add selection
		shortCutMap.put(
				(InputEvent.CTRL_DOWN_MASK<<1 ) |KeyEvent.VK_A 
				,new Runnable() {
					@Override
					public void run() {
						IntervalSelection selection = model.getActiveIntervalSelection();
						Interval<Boolean> interval = new Interval<Boolean>(
								selection.getSelectionStartInSec(), 
								selection.getSelectionEndInSec(), 
								true);
						model.addIntervalToActiveTier(interval);
						WavPanelImpl.this.repaint();
					}
				});
		//		//-- remove selection
		//		(InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK)<<1) | KeyEvent.VK_A,
		shortCutMap.put(
				(InputEvent.CTRL_DOWN_MASK<<1 ) |KeyEvent.VK_D 
				,new Runnable() {
					@Override
					public void run() {
						IntervalSelection selection = model.getActiveIntervalSelection();
						Interval<Boolean> interval = new Interval<Boolean>(
								selection.getSelectionStartInSec(), 
								selection.getSelectionEndInSec(), 
								false);
						model.addIntervalToActiveTier(interval);
						WavPanelImpl.this.repaint();
						//						model.addIntervalSelection(model.getActiveIntervalSelection());
						//						WavPanelImpl.this.repaint();
					}
				});

		//-- zoom to selection
		shortCutMap.put(
				(InputEvent.CTRL_DOWN_MASK<<1 ) |KeyEvent.VK_N 
				,new Runnable() {
					@Override
					public void run() {
						IntervalSelection selection = model.getActiveIntervalSelection();
						model.zoomTo(
								selection.getSelectionStartInSec(),
								selection.getSelectionEndInSec()	);
						WavPanelImpl.this.path=null;
						WavPanelImpl.this.repaint();
					}
				});
		//-- zoom OUT
		shortCutMap.put(
				(InputEvent.CTRL_DOWN_MASK<<1 ) |KeyEvent.VK_Q 
				,new Runnable() {
					@Override
					public void run() {
						model.zoomOut();
						WavPanelImpl.this.path=null;
						WavPanelImpl.this.repaint();
					}
				});

	}


	@Override
	public void setWavPanelModel(WavPanelModel model) {
		this.model = model;
	}



	@Override
	public void mousePressed(MouseEvent e) {
		final int x = e.getX();
		final int y = e.getY();
		if(e.isShiftDown()) { //  e.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0){ //-- shift is pushed
			model.getActiveIntervalSelection().setSelectionEndPx(x);
		}else{
			//-- check if click is within a selection
			for(int i = 0 ; i < model.getTierN();i++){
				GuiAdapterForTier<?> tier = model.getTierAdapter(i);
				if(y >= tier.getSelectionYTop() && y <= tier.getSelectionYBottom()){
					IntervalSelection sel = tier.getSelectionForX(x);
					if(sel!=null){
						model.setCursorPosToPx(sel.getSelectionEndPx());
						model.getActiveIntervalSelection().setSelectionStartSec(sel.getSelectionStartInSec());
						model.getActiveIntervalSelection().setSelectionEndSec(sel.getSelectionEndInSec());
						model.getActiveIntervalSelection().isAdjusting(false);
						repaint();
						return;
					}
					break;
				}
			}

			model.setCursorPosToPx(x);
			model.getActiveIntervalSelection().setSelectionStartPx(x);
			model.getActiveIntervalSelection().setSelectionEndPx(x);
			model.getActiveIntervalSelection().isAdjusting(true);
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int px = e.getX();
		if(model.getActiveIntervalSelection().isAdjusting()){
			px = px < 0 ? 0 : px;  
			px = px > this.getWidth() ? this.getWidth() : px;  
			model.setCursorPosToPx(px);

			model.getActiveIntervalSelection().isAdjusting(false);
			model.getActiveIntervalSelection().setSelectionEndPx(px);
		}

		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int endPx = e.getX();
		if(model.getActiveIntervalSelection().isAdjusting()){
			endPx = endPx < 0 ? 0 : endPx;  
			endPx = endPx > this.getWidth() ? this.getWidth() : endPx;  

			model.setCursorPosToPx(endPx);
			model.getActiveIntervalSelection().setSelectionEndPx(endPx);
		}
		repaint();
	}

	public void mouseMoved(MouseEvent e){	}
	public void mouseClicked(MouseEvent e){	}

	private GeneralPath calcPolyLine(){
		long t0 = System.currentTimeMillis();
		final GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO) ;

		int w = this.getWidth();
		model.setViewWidthInPx(w);
		model.setViewHeightInPx(this.getHeight());

		final double [] minMaxs = model.getWaveCurvePointsCondensed();

		path.moveTo(0, minMaxs[0]);
		for(int i = 0 ; i < (minMaxs.length/2) ; i++){
			path.lineTo(i, minMaxs[i*2+0]);
			path.lineTo(i, minMaxs[i*2+1]);
		}
		long t1 = System.currentTimeMillis();
		LOG.trace("Wav polyline created in " + (t1-t0) +"ms");
		return path;
	}

	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited( MouseEvent ignored) {}


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(path==null){
			path = calcPolyLine();
		}
		int pixelH = this.getHeight();
		int pixelW = this.getWidth();

		if(path!=null){
			Graphics2D g2 = (Graphics2D) g.create(); 
			g2.setColor(lineCol);
			g2.draw(path);
			g2.dispose();
		}

		//-- MID-LINE HORIZONTAL
		g.setColor(Color.BLACK);
		g.drawLine(0, pixelH/2, pixelW, pixelH/2);


		//-- ACTIVE SELECTION --//
		IntervalSelection selection = model.getActiveIntervalSelection();
		Graphics2D g2 = (Graphics2D) g.create(); 
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaForActiveLabel)); 

		long t0 = System.currentTimeMillis();
		int left  = selection.getSelectionStartPx();
		//-- left is the axis -> right is moving 
		int axis = left;
		int right = selection.getSelectionEndPx();
		if (left!=right){ //-- same -> no selection 
			double axisT = selection.getSelectionStartInSec();
			if(left>right){ //-- swap
				left  = left + right;
				right = left - right;
				left  = left - right;
			}
			//-- active selection
			g2.setColor(selCol);
			g2.fillRect(left, 0, right-left, pixelH);
			g2.setColor(selBorderLineCol);
			g2.drawLine(left, 0, left, pixelH);
			g2.drawLine(right, 0, right, pixelH);

			//-- time stamp

			String labelRight = String.format("%.3f", model.getActiveIntervalSelection().getSelectionEndInSec());
			Rectangle2D  rect =  timeLabelFont.getStringBounds(labelRight, g2.getFontRenderContext());

			g2.setFont(timeLabelFont);
			if(axis<right){
				g2.drawString(String.format("%.3f", axisT), axis + 2, (int)Math.ceil(rect.getHeight()));
			}else{
				g2.drawString(String.format("%.3f", axisT), axis - (int)Math.ceil(rect.getWidth()+1), (int)Math.ceil(rect.getHeight()));
			}
		}


		long t1 = System.currentTimeMillis();
		LOG.trace("Active selections created in " + (t1-t0) +"ms");
		t0 = System.currentTimeMillis();

		//-- NON-ACTIVE SELECTIONS -> LABELS  --//
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaForPassiveLabel));

		for(int tierIx = 0; tierIx < model.getTierN() ; tierIx++ ){
			//		model.getTierAdapter
			GuiAdapterForTier<?> tier = model.getTierAdapter(tierIx);


			int selectionYTop = tier.getSelectionYTop();
			int selectionBottomY =  tier.getSelectionYBottom();  /* vertically symmetric: pixelH-selectionMarginTop; */
			int selectionHeight    = selectionBottomY - selectionYTop;   //40;//pixelH-2*selectionMarginTop;
			int [] rgb = tier.getSelectionFillColorInRgb();
			Color selBgCol = new Color(rgb[0], rgb[1], rgb[2]);


			if(Boolean.class.equals(tier.getTierType())){
				for(int i = 0 ; i < tier.getSelectionN() ; i++){
					IntervalSelection sel = tier.getSelectionX(i);
					left = sel.getSelectionStartPx();
					right = sel.getSelectionEndPx();

					String labelLeft = String.format("%.3f", sel.getSelectionStartInSec());
					String labelRight = String.format("%.3f", sel.getSelectionEndInSec());
					Rectangle2D  rect =  timeLabelFont.getStringBounds(labelRight, g2.getFontRenderContext());
//
					g2.setColor(selBgCol);
					g2.fillRect(left, selectionYTop, right-left, selectionHeight);
					g2.setColor(selBorderLineCol);
					g2.drawLine(left,  selectionYTop, left,  selectionBottomY);
					g2.drawLine(right, selectionYTop, right, selectionBottomY);
					g2.drawLine(left,  selectionYTop, right, selectionYTop);
					g2.drawLine(left,  selectionBottomY,        right, selectionBottomY);
//
//
//					//-- bottom-left
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaForActiveLabel)); 
					g2.setColor(selBorderLineCol);
					g2.setFont(timeLabelFont);
					g2.drawString(labelLeft, left+1,  selectionYTop+(int)Math.ceil(rect.getHeight())+1 );

					//-- top-right
					g2.setFont(timeLabelFont);
					g2.drawString(labelRight, 
							right - (int)Math.ceil(rect.getWidth()+1) //-- adjust  text width
							, selectionYTop+(int)Math.ceil(rect.getHeight())+1 //-- adjust for text height
							);
				}
				t1 = System.currentTimeMillis();
				LOG.trace( tier.getTier().getTierType()  +" tier selections created in " + (t1-t0) +"ms");
				t0 = System.currentTimeMillis();
			}

			if(VadError.class.equals(tier.getTierType())){
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));


				VadErrorTier errTier = (VadErrorTier)tier.getTier();

				//-- horizontal line for error --//
				g2.setColor(Color.RED);
				g2.drawLine(0,  selectionYTop, pixelW,  selectionYTop);
				g2.drawLine(0,  selectionBottomY, pixelW,  selectionBottomY);
				g2.setFont(errLabelFont);

				for(int i = 0; i < tier.getSelectionN(); i++){
					IntervalSelection sel = tier.getSelectionX(i);
					VadError err = errTier.getIntervalX(i).label;

					left = sel.getSelectionStartPx();
					right = sel.getSelectionEndPx();
					g2.drawLine(left,  selectionYTop, left,  selectionBottomY);
					g2.drawLine(right,  selectionYTop, right,  selectionBottomY);
					g2.drawString(err.name(),left, selectionBottomY); 
				}
				t1 = System.currentTimeMillis();
				LOG.trace( tier.getTier().getTierType()  +" tier selections created in " + (t1-t0) +"ms");
				t0 = System.currentTimeMillis();
			}
		}
		t1 = System.currentTimeMillis();
		LOG.trace("Passive selections created in " + (t1-t0) +"ms");



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



		//-- CURSOR --//
		final int cursorX = model.getCursorPositionInPx();
		g.setColor(cursorCol);
		g.drawLine(cursorX, 0, cursorX, pixelH);

		g.setFont(timeLabelFont);
		g.drawString(String.format("%.3f", model.getCursorPositionInSec()), cursorX + 2, pixelH-1);


		//-- WINDOW time labels --//
		g.setColor(Color.DARK_GRAY);
		g.setFont(timeLabelFont);
		g.drawString(String.format("%.3f", model.getSecFromPx(0)), 0+ 2, pixelH-1);

		String labelRight = String.format("%.3f", model.getSecFromPx(pixelW));
		Rectangle2D  rect =  timeLabelFont.getStringBounds(labelRight, g2.getFontRenderContext());
		g.drawString(labelRight
				, pixelW-(int)Math.ceil(rect.getWidth()+1)
				, (int)Math.ceil(rect.getHeight())    );
	}




	public void componentResized(ComponentEvent arg0) {
		this.path = null;
		model.setViewWidthInPx(this.getWidth());
		model.setViewHeightInPx(this.getHeight());
	}
	public void componentHidden(ComponentEvent ignore) {}
	public void componentMoved(ComponentEvent ignore){}
	public void componentShown(ComponentEvent ignore) {}



	@Override
	public void keyReleased(KeyEvent ignore){ }
	@Override
	public void keyTyped(KeyEvent ignore) {	}
	@Override
	public void keyPressed(KeyEvent e){
		int key = ( e.getModifiers() << 7) | e.getKeyCode();

		Runnable shortcutAction = shortCutMap.get(key);
		if(shortcutAction!=null){
			//			LOG.trace("Action found for shortcut {} " + e.getKeyCode());
			shortcutAction.run();
		}else{
			//			LOG.trace("No action found for shortcut {} " + e.getKeyCode());
		}
	}


	
	@Override
	public void startGui(){
		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);	

		JFrame frame = new JFrame("Amplitude threshold VAD");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setSize(800, 400);
		//		frame.pack();
		frame.setVisible(true);

	}
	
}




