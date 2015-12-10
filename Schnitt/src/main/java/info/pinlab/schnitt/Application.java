package info.pinlab.schnitt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.gui.WavGraphics;
import info.pinlab.schnitt.gui.WavPanelModel;
import info.pinlab.schnitt.gui.WavPanelUI;
import info.pinlab.schnitt.gui.swing.WavPanelImpl;

public class Application {
	public static Logger LOG = LoggerFactory.getLogger(Application.class);
	
	WavPanelModel wavPanelModel;
	WavPanelUI wavPanelView;
	
	Application(){
		wavPanelModel = new WavGraphics();
		wavPanelView = new WavPanelImpl();
		wavPanelView.setWavPanelModel(wavPanelModel);
	}
	
	Application(WavClip wav){
		this();
		wavPanelModel.setWav(wav); //-- !!
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
