package info.pinlab.schnitt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.gui.WavGraphics;
import info.pinlab.schnitt.gui.WavPanelModel;
import info.pinlab.schnitt.gui.WavPanelUI;
import info.pinlab.schnitt.gui.swing.WavPanelImpl;
import info.pinlab.schnitt.io.AudioWithTiers;
import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.vad.BinaryTargetTier;

public class Application {
	public static Logger LOG = LoggerFactory.getLogger(Application.class);
	
	WavPanelModel wavPanelModel;
	WavPanelUI wavPanelView;
	
	public Application(){
		wavPanelModel = new WavGraphics();
		wavPanelView = new WavPanelImpl();
		wavPanelView.setWavPanelModel(wavPanelModel);
	}
	
	public Application(WavClip wav){
		this();
		wavPanelModel.setWav(wav); //-- !!
	}
	
	public Application(AudioWithTiers awt){
		this();
		wavPanelModel.setWav(awt.getWav());
		BinaryTargetTier targ = (BinaryTargetTier)awt.getTarg();
		if(targ!=null){
			wavPanelModel.addTier(targ);
		}
	}
	
	public void start(){
		wavPanelView.startGui();
	}
	
	
	public static void main(String[] args) throws Exception {
		WavClip wav = new WavClip(Main.class.getResourceAsStream("sample.wav"));
		Application app = new Application(wav);
		app.start();
	}
	
}
