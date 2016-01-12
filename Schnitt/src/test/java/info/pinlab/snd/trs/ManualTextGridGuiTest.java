package info.pinlab.snd.trs;

import java.io.InputStream;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.gui.WavGraphics;
import info.pinlab.schnitt.gui.WavPanelModel;
import info.pinlab.schnitt.gui.swing.WavPanelImpl;
import info.pinlab.snd.fe.FEParam;
import info.pinlab.snd.fe.FrameAlignment;
import info.pinlab.snd.fe.ParamSheet;
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
				.set(FEParam.FRAME_LEN_MS, 100)
				.set(FEParam.FRAME_SHIFT_MS, 5)
				.build();
		StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(params);
		fe.setWav(wav);
		fe.start();
		DoubleFrameTier feat = fe.getFrameTier();
		
		//-- creates target shift_len resolution
		//-- step 1: calculate overlap
		FrameAlignment align = new FrameAlignment(feat, target);
		align.calcOverlap("overlap");
		//-- step 2: decides which frame to keep (by thresh) 
		ThresholdVad vad = new ThresholdVad();
		vad.setParam(ThresholdVad.THRESH, 0.5);
		vad.setParam(ThresholdVad.THRESH_TARG, "overlap");
		vad.setFrameTier(feat);
		BinaryHypoTier hypo = vad.getHypo();
		
//		System.out.println(hypo);
		
		//-- Start GUI
		WavPanelModel model = new WavGraphics();
		model.setWav(wav);
		model.addTier(target);
		model.addTier(hypo);
//		model.zoomTo(37.35d, 40.6d);
		
		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);
		panel.startGui();
	}

}
