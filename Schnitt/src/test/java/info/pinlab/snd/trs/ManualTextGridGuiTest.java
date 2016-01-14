package info.pinlab.snd.trs;

import java.io.InputStream;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.gui.WavGraphics;
import info.pinlab.schnitt.gui.WavPanelModel;
import info.pinlab.schnitt.gui.swing.WavPanelImpl;
import info.pinlab.snd.fe.FEParam;
import info.pinlab.snd.fe.FrameAlignment;
import info.pinlab.snd.fe.ParamSheet;
import info.pinlab.snd.fe.PowerCalculator;
import info.pinlab.snd.fe.ParamSheet.ParamSheetBuilder;
import info.pinlab.snd.fe.StreamingAcousticFrontEnd;
import info.pinlab.snd.vad.BinaryHypoTier;
import info.pinlab.snd.vad.BinaryTargetTier;
import info.pinlab.snd.vad.ThresholdVad;



/**
 * plots textgrid + sound file
 * 
 * @author kinoko
 *
 */
public class ManualTextGridGuiTest {

	
	public static BinaryHypoTier getAlignedTarget(DoubleFrameTier frameTier, BinaryTargetTier target){
		//-- creates target shift_len resolution
		//-- step 1: calculate overlap
		FrameAlignment align = new FrameAlignment(frameTier, target);
		align.calcOverlap("overlap");
		
		//-- step 2: decides which frame to keep (by thresh) 
		ThresholdVad vad = new ThresholdVad();
		vad.setParam(ThresholdVad.THRESH, 0.5);
		vad.setParam(ThresholdVad.THRESH_TARG, "overlap");
		vad.setFrameTier(frameTier);
		return vad.getHypo();
	}
	

	public static BinaryHypoTier getPowerHypo(DoubleFrameTier frameTier){
		ThresholdVad vad = new ThresholdVad();
		vad.setParam(ThresholdVad.THRESH, 0.004);
		vad.setParam(ThresholdVad.THRESH_TARG, "power");
		vad.setFrameTier(frameTier);
		return vad.getHypo();
	}
	
	
	
	public static void main(String[] args) throws Exception{
		String gridFileName = "sample.TextGrid";
		String wavFileName = "sample.wav";
		
		WavClip wav = new WavClip(TextGridAdapterTest.class.getResourceAsStream(wavFileName));
		
		InputStream is = TextGridAdapterTest.class.getResourceAsStream(gridFileName);
		LabelTier tier = TextGridAdapter.fromTextGrid(is);
		BinaryTargetTier target = new BinaryTargetTier(TextGridAdapter.toBinaryTier(tier));
		

		//-- calculate features frame timestamps (no actual processing
		ParamSheet params = new ParamSheetBuilder()
				.set(FEParam.HZ, 16000)
				.set(FEParam.FRAME_PROCESSORS, PowerCalculator.class.getName())
				.set(FEParam.FRAME_LEN_MS, 120)
				.set(FEParam.FRAME_SHIFT_MS, 20)
				.build();
		StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(params);
		fe.setWav(wav);
		fe.start();
		DoubleFrameTier feat = fe.getFrameTier();
		
		
//		BinaryHypoTier hypo = getAlignedTarget(feat, target);
		BinaryHypoTier hypo = getPowerHypo(feat);
		
		
		
//		System.out.println(hypo);
		
		//-- Start GUI
		WavPanelModel model = new WavGraphics();
		model.setWav(wav);
		model.addTier(target);
		model.addTier(hypo);
		model.zoomTo(37.35d, 40.6d);
		
		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);
		panel.startGui();
	}

}
