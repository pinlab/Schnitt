package info.pinlab.snd.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.WavUtil;
//import info.pinlab.snd.WavUtil;
import info.pinlab.snd.gui.swing.WavPanelImpl;
import info.pinlab.snd.trs.Tier;

public class WavPanelControl {
	public static Logger logger = LoggerFactory.getLogger(WavPanelControl.class); 
	private WavPanelUI view = null;
	private WavClip wav = null;
	private int hz = 0;
	private Map<String, Tier> tiers = new HashMap<String, Tier>();

	private int [] samples = null;

	
	
	
	public WavPanelControl(){
	}
	
	
		
	public void setWavPanel(WavPanelUI view){
		this.view = view;
	}
	

	
	public void zoomTo(int startSampleIx, int endSampleIx){
//		if(samples==null){ //-- sanity check
//			logger.warn("Can't zoom with no samples set!");
//			return;
//		}
//		System.out.println("Size " + samples.length);
//		
//		logger.info("Size " + samples.length);
//		//TODO: implement without copy
//		int len = endSampleIx-startSampleIx;
//		int []clone = new int[len];
//		System.arraycopy(samples, startSampleIx, clone, 0, len);
//		
//		for(int i = 0 ; i < len;i++){
////			System.out.println(clone[i]);
//		}
//		
//		if(view!=null){
//			view.setSampleArray(clone, hz);
//		}
	}
	
	

	
	public void setWavClip(WavClip wav){
		this.wav = wav;
		//-- set all samples
		samples = WavUtil.getIntArray(wav);
//		samples = this.wav.toIntArray();
		this.hz = (int)wav.getAudioFormat().getSampleRate();
		if(view!=null){
//			view.setSampleArray(samples, hz);
		}
	}

	
	public void setWavPane(WavPanelUI pane){
		this.view = pane;
	}
	
	public void addTier(Tier tier){
//		String tierName = tier.getName();
//		if(tierName==null || tierName.isEmpty()){
//			tierName = String.format("tier%02d", tiers.size());
//		}
//		if(tiers.containsKey(tierName)){
//			throw new IllegalStateException("Already defined tier! '" + tierName +"'");
//		}
//		tiers.put(tierName, tier);
//		if(view != null){
//			view.addTier(tier);
//		}
	}
	
	
	public static void main(String[] args) throws Exception{
		//-- this should go to test class
		
		WavClip wav = new WavClip(WavPanelControl.class.getResourceAsStream("verylongsample.wav"));
		
		WavPanelControl control = new WavPanelControl();
		WavPanelImpl view = new WavPanelImpl();
		control.setWavPane(view);
		control.setWavClip(wav);
//		control.zoomTo(2000, 3000);
		
		
		JFrame frame = new JFrame("WavPanel test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(view);
		frame.setSize(800, 400);
//		frame.pack();
		frame.setVisible(true);

		
	}
}
