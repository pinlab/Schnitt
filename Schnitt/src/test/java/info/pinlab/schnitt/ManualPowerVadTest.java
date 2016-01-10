package info.pinlab.schnitt;

import java.util.concurrent.CountDownLatch;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.gui.WavGraphics;
import info.pinlab.schnitt.gui.WavPanelModel;
import info.pinlab.schnitt.gui.swing.WavPanelImpl;
import info.pinlab.snd.fe.DoubleFrame;
import info.pinlab.snd.fe.FEParam;
import info.pinlab.snd.fe.FEParamString;
import info.pinlab.snd.fe.FeatureSink;
import info.pinlab.snd.fe.FrameProcessor;
import info.pinlab.snd.fe.ParamSheet;
import info.pinlab.snd.fe.ParamSheet.ParamSheetBuilder;
import info.pinlab.snd.fe.PowerCalculator;
import info.pinlab.snd.fe.StreamingAcousticFrontEnd;
import info.pinlab.snd.trs.DoubleFrameTier;
import info.pinlab.snd.vad.BinaryHypoTier;
import info.pinlab.snd.vad.BinaryTargetTier;
import info.pinlab.snd.vad.ThresholdVad;

public class ManualPowerVadTest implements FeatureSink{
	
	private DoubleFrameTier tier ;
	@Override
	public void add(DoubleFrame frame) {
		tier.add(frame);
	}
	@Override
	public void end() {
		synchronized (this) {
			latch.countDown();
			notifyAll();
		}
	}
	
	CountDownLatch latch = new CountDownLatch(1);
	

	public static void main(String[] args) throws Exception{
		WavClip wav = new WavClip(ManualPowerVadTest.class.getResourceAsStream("long-sample.wav"));
		BinaryTargetTier targ = new BinaryTargetTier();

		ManualPowerVadTest tester = new ManualPowerVadTest();
		
		System.out.println(wav.getDurInFrames());
		
		
		
		ParamSheet context = new ParamSheetBuilder()
				.set(FEParam.HZ, 16000)
				.set(FEParam.FRAME_LEN_MS, 500)
				.set(FEParam.FRAME_SHIFT_MS, 100)
				.set(FEParam.FRAME_PROCESSORS, PowerCalculator.class.getName())
				.build();

		tester.tier = new DoubleFrameTier(16000, context.get(FEParamString.FRAME_LEN_MS));
		
		System.out.println(context.get(FEParam.FRAME_LEN_SAMPLE) +" samples per frame");
		System.out.println(context.get(FEParam.FRAME_SHIFT_MS) +" samples per frame");
		
		
		
		StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(context);
		fe.setSink(tester);
		fe.setWav(wav);
		
		
		
		System.out.print("Pipeline : " );
		for(FrameProcessor proc : fe.getProcessorPipeline()){
			System.out.println( proc.getPredecessorKey() + " -> " + proc.getKey());
		}
		fe.start();
		tester.latch.await();
		

		
		
		ThresholdVad vad = new ThresholdVad();
		vad.setFrameTier(tester.tier);
		vad.setParam(ThresholdVad.THRESH_TARG, "power");
		vad.setParam(ThresholdVad.THRESH, 0.40E-02);
		BinaryHypoTier hypo = vad.getHypo();
		

		//-- GUI
		WavPanelModel model = new WavGraphics();
		model.setWav(wav);
		model.addTier(hypo);

		WavPanelImpl panel = new WavPanelImpl();
		panel.setWavPanelModel(model);
		panel.startGui();

	}







}
