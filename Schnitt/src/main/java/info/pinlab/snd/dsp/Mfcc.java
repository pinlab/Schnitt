package info.pinlab.snd.dsp;

import info.pinlab.pinsound.WavClip;

public interface Mfcc {

	public void setWav(WavClip wav);
	
//	public Iterator<List<Double>> iterator();
	public void next(double [] buff);
	public boolean hasNext();
	
	
//	AcousticForntEnd fe = new... .build();
//	
//	fe.setWav(wav);
//	
//	while(fe.hasNext()){
//		fe.getWindowFft();
//		fe.getWindowMfcc();
//		fe.next();
//	}
//	
}


/*

	Mfcc mfcc = new Mfcc();
	Iterator it = mfcc.iterator()
	while(it.hasNext()){
		Double [] coefs = it.next();
	}



*/