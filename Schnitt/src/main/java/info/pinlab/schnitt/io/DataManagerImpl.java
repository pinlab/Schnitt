package info.pinlab.schnitt.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.pinlab.pinsound.WavClip;
import info.pinlab.schnitt.Application;
import info.pinlab.snd.vad.BinaryTargetTier;

public class DataManagerImpl implements DataManager {

	
	@Override
	public AudioWithTiers readFile(String wavPath) throws IOException{
		WavClip wav = new WavClip(wavPath);
		AudioWithTiers awt = new AudioWithTiers(wav);
		
		
		String tierPath = FileNameManip.dirNameToTextGridFilePath(wavPath);
		if(tierPath!=null){
			System.out.println(tierPath);
			BinaryTargetTier target = DataToBinaryTier.TextGridToBinaryTier(tierPath);
			awt.setTarget(target);
		}
		return awt;
	}

	
	@Override
	public List<AudioWithTiers> parseDir(String dir, boolean isRrecursive){
		List<AudioWithTiers> awts = new ArrayList<AudioWithTiers>();
		
		File rootDir = new File(dir);
		if(!rootDir.isDirectory()){
			return awts;
		}

		//-- Check wav files
		for(File f: rootDir.listFiles()){
			String fname = f.getName().toLowerCase();
			if(f.isFile() && fname.endsWith(".wav")){
				//-- wav file
				System.out.println(f.getName());
				try{
					AudioWithTiers awt = readFile(f.getAbsolutePath());
					awts.add(awt);
				}catch(IOException e){
					//-- pass
				}
			}
		}
		return awts;
	}
	
	@Override
	public List<AudioWithTiers> parseDir(String dir) {
		return parseDir(dir, false);
	}
	
	
	
	static void checkWithGui(AudioWithTiers awt) throws Exception{
		WavClip wav = awt.getWav();
		Application app = new Application(wav);
		app.start();
	}

	
	
	public static void main (String [] args) throws Exception{
		DataManagerImpl dataManager = new DataManagerImpl();
		
//		String wavPath = "C:\\Users\\Naoi\\Desktop\\VAD-ay2015-uw-pre-Working\\1376470_ay2015-uw-pre-TaskSet05_005_1_[20150824-150909].wav";
		String wavPath = "C:\\Users\\Naoi\\Desktop\\VAD-ay2015-uw-pre-Working\\1392739_ay2015-uw-pre-TaskSet05_006_1_[20150824-150818].wav";
		
		AudioWithTiers awt = dataManager.readFile(wavPath);
//		System.out.println("Wav -> Target load test");
		
		
		String dir = "C:\\Users\\Naoi\\Desktop\\VAD-ay2015-uw-pre-Working\\";
//		List<AudioWithTiers> awts = dataManager.parseDir(dir);
//		System.out.println(awts.size());
		
//		Application app = new Application(awt);
//		app.start();

		
	}




}
