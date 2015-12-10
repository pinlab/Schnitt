package info.pinlab.schnitt.gui;

import java.io.File;
import java.io.IOException;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import info.pinlab.pinsound.WavClip;


/**
 * 
 * Manages data for table view.
 * It loads and unloads data.
 * 
 * @author Gabor PINTER
 *
 */
public class WavTableData {
	
	private final EventList<WavTableRow> rows ;

	
	
	public WavTableData(){
		rows = GlazedLists.threadSafeList(new BasicEventList<WavTableRow>());
	}
	
	
	
	public void addWavFile(String path) throws IOException{
		WavClip wav = new WavClip(path);
		String fname = new File(path).getName();
		
		rows.getReadWriteLock().writeLock().lock();
		rows.add(new WavTableRow(wav, fname));
		rows.getReadWriteLock().writeLock().unlock();
	}
	

	public static void main(String[] args) throws Exception{
		String [] wavs = new String []{		
		 "/home/kinoko/workspace/schnitt/WavPanel/src/main/resources/info/pinlab/snd/gui/sample.wav"
		,"/home/kinoko/workspace/schnitt/WavPanel/src/main/resources/info/pinlab/snd/gui/longsample.wav"
		,"/home/kinoko/workspace/schnitt/WavPanel/src/main/resources/info/pinlab/snd/gui/verylongsample.wav"
		};
		
		
		
		WavTableData model = new WavTableData();
		for(String wav: wavs){
			model.addWavFile(wav);
		}
	}
	
	public EventList<WavTableRow> getEventList(){
		return rows;
	}
	
	

}
