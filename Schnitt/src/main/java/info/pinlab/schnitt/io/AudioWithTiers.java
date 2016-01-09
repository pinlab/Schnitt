package info.pinlab.schnitt.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.vad.VadErrorTier;

/**
 * Holds audio with hypo and target tiers.  
 * 
 * @author Gabor Pinter
 *
 */
public class AudioWithTiers{
	public static Logger LOG = LoggerFactory.getLogger(AudioWithTiers.class);

	private WavClip wav = null;
	private BinaryTier target = null;
	private VadErrorTier err = null;
	private BinaryTier hypo;


	public AudioWithTiers(WavClip wav){
		this.wav = wav;
	}

	public void addHypo(BinaryTier hypo){
		if(hypo==null){
			LOG.error("Hypo tier argument can't be null!");
			return;
		}
		this.hypo = hypo;
		if(target!=null){
			err = new VadErrorTier(this.target, this.hypo);
		}
	}

	public void setTarget(BinaryTier targ){
		if(targ==null){
			LOG.error("Hypo tier argument can't be null!");
			return;
		}
		this.target = targ;
		if(this.hypo!=null){
			err = new VadErrorTier(this.target, this.hypo);
		}
	}

	public void setWav(WavClip wav){
		this.wav = wav;
	}
	public WavClip getWav(){
		return this.wav;
	}
	public BinaryTier getTarg(){
		return target;
	}

//	public void setVadErrTier(VadErrorTier err){
//		this.err = err;
//	}
	
	public VadErrorTier getVadErrTier(){
		return this.err;
	}
}




