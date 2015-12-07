package info.pinlab.schnitt;

import java.util.ArrayList;
import java.util.List;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.gui.WavGraphics;
import info.pinlab.schnitt.gui.WavPanelModel;
import info.pinlab.schnitt.gui.swing.WavPanelImpl;
import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.Tier.Type;
import info.pinlab.snd.trs.VadErrorTier;
import info.pinlab.snd.vad.AmplitudeVad;
import info.pinlab.snd.vad.VadError;

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
		model.setSampleArray(wav.toIntArray(), (int)wav.getAudioFormat().getSampleRate());
		//		System.out.println("LENGTH " + samples.length);
		//		model.setSampleArray(
		//				WavUtil.getIntArray(wav)
		////				wav.toIntArray(), 
		//				,(int)wav.getAudioFormat().getSampleRate());

		@SuppressWarnings("unchecked")
		//		IntervalTier<Boolean> hypo = (IntervalTier<Boolean>)model.getTierAdapter(0).getTier();

		BinaryTier target = new BinaryTier(Type.TARG);
		target.addInterval(0.455, 1.875, true);
		model.addTier(target, Boolean.class);


		AmplitudeVad vad = new AmplitudeVad();
		BinaryTier hypo = vad.getVoiceActivity(wav);
		//		hypo.addInterval(0.5, 1.5, true);

		System.out.println(hypo.size());
		

		model.addTier(hypo, Boolean.class);

		VadErrorTier vadTier = new VadErrorTier(target, hypo);
		model.addTier(vadTier, VadError.class);

		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);
		panel.startGui();


	}

}
