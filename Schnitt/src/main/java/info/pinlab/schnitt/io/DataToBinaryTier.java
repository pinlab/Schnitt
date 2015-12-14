package info.pinlab.schnitt.io;

import java.util.List;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.vad.BinaryTargetTier;

public class DataToBinaryTier {

	public static  BinaryTargetTier TextGridToBinaryTier(String path){

		List<String> lines =  TextReader.textToList(path);

		//creating arrays consisting max, mind and text

		double [] dmax = TextReader.getMaxArray(lines);
		double [] dmin = TextReader.getMinArray(lines);
		String [] text = TextReader.getText(lines);


		BinaryTargetTier target = new BinaryTargetTier();

		target.addInterval(dmin[0],dmax[dmax.length-1], false);
		for(int i=0; i<text.length ;i++){
			if(text[i].equals("\"s\"")) //"s" means speaking(active)
				target.addInterval(dmin[i],dmax[i], true);
		}	

		return target;

	}


}
