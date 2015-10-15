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
import info.pinlab.snd.gui.WavPanel;
import info.pinlab.snd.trs.Tier;


/**
 * Swing's JPanel implementation of WavPanel. 
 * 
 * @author kinoko
 *
 */
public class WavPanelImpl extends JPanel 
			implements  WavPanel,
			MouseListener, 
			MouseMotionListener,
			ComponentListener
{
	private static final long serialVersionUID = -1689117249151777252L;
	
	private static Logger logger = LogManager.getLogger(WavPanelImpl.class);
	
	private int [] samples = null;
	private int hz = 0;
	
	//-- COLORS --//
	private Color bgCol = Color.WHITE; //new Color(243,243,248); //Color.BLACK;
	private Color fontCol = new Color(126, 179, 102);
	private Color labelBgCol = new Color(204, 215, 119);
	private Color cursorCol = new Color(126, 179, 102); //new Color(119, 127, 0);//Color.white;
	static private Color lineCol = new Color(100,100,220);;
	private Color selCol = new Color(214, 255, 161); // greenish;
	
	
	
	public WavPanelImpl(){
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addComponentListener(this);

		
		this.setOpaque(true);
		this.setBackground(bgCol);
	}


	
	
	
	

	
	
	public void setTier(Tier tier) {
	}
	public void setSampleArray(int [] samples, int hz) {
		this.samples = samples;
		this.hz = hz;
		
		
	}

	
	
	
	
	
	
	private GeneralPath calcPolyLine(){
		if (samples==null){
			return null;
		}
		int pixelN = this.getWidth();
		int pixelH = this.getHeight();
		
		GeneralPath path = new GeneralPath() ;

		if(samples.length > pixelN){ //-- more pixels than samples
			double spanSize = samples.length / (double)pixelN;
//			System.out.println("Pixel #=" + pixelN);
			
			
			int sampleMin = samples[0]; 
			int sampleMax = samples[0];
			int [] spanMins = new int[pixelN]; 
			int [] spanMaxs = new int[pixelN]; 
			
			
			int j = 1;
			for(int i = 0; i < pixelN ; i++){ //-- let's calc every pixel!
//				j--;
				int jMax = (int)Math.round((i+1)*spanSize);
//				System.out.println("  " + j +  "..." + jMax);

				int spanMin = samples[j];
				int spanMax = samples[j];
				while(j <= jMax && j < samples.length){
					spanMin = samples[j] < spanMin ? samples[j] : spanMin; 
					spanMax = samples[j] > spanMax ? samples[j] : spanMax; 
					j++;
				}
				spanMins[i] = spanMin;
				spanMaxs[i] = spanMax;
				sampleMin = (spanMin < sampleMin) ? spanMin : sampleMin;
				sampleMax = (spanMax > sampleMax) ? spanMax : sampleMax;
			}
			
//			sampleMin = (-sampleMax < sampleMin)? -sampleMax : sampleMin;
//			sampleMax = (-sampleMin > sampleMax)? -sampleMin : sampleMax;
			sampleMax *= 1.05; //-- give some padding
			sampleMin *= 1.05; //-- give some padding 
			
			
			double range = 1.0*(sampleMax - sampleMin)/pixelH;
			for(int i = 0 ; i < pixelN ; i++){
				spanMaxs[i] =(int)Math.ceil( (spanMaxs[i]-sampleMin)/range) ; 
				spanMins[i] =(int)Math.floor((spanMins[i]-sampleMin)/range);
				path.moveTo(i, spanMins[i]);
				path.lineTo(i, spanMaxs[i]);
			}
		}else{
			
		}
		return path;
//		System.out.println(this.getWidth() +"\t" + samples.length);
		
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
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Clicked");
	}
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		GeneralPath path = calcPolyLine();
		
		if(path!=null){
//			Graphics2D g2 = (Grahpics2D)g;
			Graphics2D g2 = (Graphics2D) g.create(); 
			g2.setColor(lineCol);
			g2.draw(path);
			g2.dispose();
		}
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






	
	
	
}
