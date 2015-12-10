package info.pinlab.schnitt;

import java.util.ArrayList;
import java.util.List;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.gui.WavGraphics;
import info.pinlab.schnitt.gui.WavPanelModel;
import info.pinlab.schnitt.gui.swing.WavPanelImpl;
import info.pinlab.snd.trs.VadErrorTier;
import info.pinlab.snd.vad.AmplitudeVad;
import info.pinlab.snd.vad.BinaryHypoTier;
import info.pinlab.snd.vad.BinaryTargetTier;

public class Main {

	
	public static void main(String[] args) throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		list.add(0);
		list.add(1);
		list.add(2);
		System.out.println(list.size());
		list.remove(0);
		list.add(0, 8);		
		System.out.println(list.size());

		WavClip wav = new WavClip(Main.class.getResourceAsStream("sample.wav"));
		//		WavClip wav = new WavClip(WavPanelImpl.class.getResourceAsStream("longsample.wav"));
		//		WavClip wav = new WavClip(WavPanelImpl.class.getResourceAsStream("verylongsample.wav"));

		WavPanelModel model = new WavGraphics();
		model.setWav(wav);
		

		
		BinaryTargetTier target = new BinaryTargetTier();
		target.addInterval(0.4, 1.8, true);
		model.addTier(target);


		AmplitudeVad vad = new AmplitudeVad();
		BinaryHypoTier hypo = vad.getVoiceActivity(wav);
		hypo.addInterval(0.5, 1.5, true);
		model.addTier(hypo);

		
		VadErrorTier vadTier = new VadErrorTier(target, hypo);
		model.addTier(vadTier);

		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);
		panel.startGui();


	}

}
