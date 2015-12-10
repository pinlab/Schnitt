package info.pinlab.schnitt.gui;

import java.io.InputStream;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.gui.swing.WavPanelImpl;

public class ManualTestZeroLine {

	public static void main(String[] args) throws Exception{
		String wavFileName = "20150824-150917.wav";
		InputStream is = ManualTestZeroLine.class.getResourceAsStream(wavFileName);
		WavClip wav = new WavClip(is);
		
		
		WavPanelModel model = new WavGraphics();
		model.setWav(wav);
		
		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);
		panel.startGui();
		
	}

}
