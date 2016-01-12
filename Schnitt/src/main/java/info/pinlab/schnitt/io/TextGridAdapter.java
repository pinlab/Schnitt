package info.pinlab.schnitt.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.snd.trs.LabelTier;


/**
 * 
 * Reading Praat's textgrid into LabelTier
 * 
 * @author Naoi
 *
 */
public class TextGridAdapter {
	public static Logger LOG = LoggerFactory.getLogger(TextGridAdapter.class);

	/**
	 * Reads text grid from file
	 * @param path
	 * @return
	 */
	public static LabelTier fromTextGrid(String path) throws FileNotFoundException{
		File gridPath = new File(path);
		LOG.info("Opening file '" + gridPath.getAbsolutePath() + "'");
		InputStream is = new FileInputStream(gridPath);
		return fromTextGrid(is);
	}
	
	
	/**
	 * Read TextGrid file from input stream (such as bundled jar).
	 * 
	 * @param is
	 * @return {@link LabelTier} 
	 */
	public static LabelTier fromTextGrid(InputStream is){
		//-- make sure it's UTF-8!
		if(is==null){
			LOG.error("'fromTextGrid' received null arg"); 
			throw new IllegalArgumentException("Argument can't be null!");
		}
		int lineX = 0;
		LabelTier tier = new LabelTier();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line ;
            while((line  = br.readLine()) != null){
            	lineX++;
            	//: parse textgrid into tier!

            	
            	
            	
            	
            	
            }
		} catch (UnsupportedEncodingException ignore){ //-- "UTF-8" is supported
		} catch (IOException e){
			String msg = "IO error when trying to read line " + lineX ;
			LOG.error(msg);
			return null;
		}

		return tier;
	}
	

	
	public static String toTextGrid(LabelTier tier){
		StringBuffer sb = new StringBuffer();
		//TODO: write into TextGrid
		
		
		
		
		return sb.toString();
	}
	
	
	
	
	public static void main(String[] args) {
	}
}

