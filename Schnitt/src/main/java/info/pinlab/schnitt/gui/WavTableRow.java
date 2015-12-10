package info.pinlab.schnitt.gui;

import java.util.HashMap;
import java.util.Map;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.trs.Tier;


/**
 * Data holder for a single data row in the Wav Table. 
 * 
 * @author kinoko
 *
 */
public class WavTableRow {
	final WavClip wav;
	final String durLabel;
	final String fileNameLabel; 
	final int serial;
	static int serials = -1;
	
	
	private final Map<String, Tier> tiers;
	
	public WavTableRow(WavClip wav, String fname){
		this.wav = wav;
		durLabel = String.format("%.4f", this.wav.getDurInMs() / 1000.0d);
		this.fileNameLabel = fname;
		
		tiers = new HashMap<String, Tier>();
		serial = serials++;
	}
	
	public void addTier(Tier tier){
		tiers.put(tier.getName(), tier);
	}

	
	public String getLabelForDur(){
		return durLabel;
	}
	
	public String getLabelForFname(){
		return fileNameLabel;
	}
	
	public int getTierN(){
		return tiers.size();
	}


	
	
	
}
