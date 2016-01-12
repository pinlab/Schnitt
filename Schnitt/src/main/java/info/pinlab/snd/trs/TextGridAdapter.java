package info.pinlab.snd.trs;

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


/**
 * 
 * Read/Write adapter for Praat's textgrid.
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
			Double max = null;
			Double min = null;
			String text = null;
			
			while((line  = br.readLine()) != null){
				lineX++;
				//: parse textgrid into tier!
				line = line.trim();
				
				if(line.startsWith("xmax")){
					if(text != null){
						br.close();
						String err = "Parsing error: extra text field before #"+lineX +"!";
						LOG.error(err);
						throw new IOException(err);
					}
					int eqAt = line.indexOf("=");
					max = Double.parseDouble(line.substring((eqAt+1), line.length()));
				}else
				if(line.startsWith("xmin")){
					if(text != null){
						br.close();
						String err = "Parsing error: extra text field before #"+lineX +"!";
						LOG.error(err);
						throw new IOException(err);
					}
					int eqAt = line.indexOf("=");
					min = Double.parseDouble(line.substring((eqAt+1), line.length()));
				}else
				if(line.startsWith("text")){
					if (min==null || max == null){
						br.close();
						String err = "Parsing error: min or max value is missing before text! #"+lineX;
						LOG.error(err);
						throw new IOException(err);
					}
					text = line.substring((line.indexOf("\"")+1), line.lastIndexOf("\"")).trim();
					tier.addInterval(min, max, text);
					//-- reset 
					min = null;
					max = null;
					text = null;
				}
			}
		} catch (UnsupportedEncodingException ignore){ //-- "UTF-8" is supported
		} catch (IOException e){
			String msg = "IO error when trying to read line " + lineX ;
			LOG.error(msg);
			return null;
		}finally {
			try {
				is.close();
			} catch (IOException ignore) {	}
		}
		return tier;
	}


	public static String toTextGrid(LabelTier tier){
		StringBuffer sb = new StringBuffer();
		//TODO: write into TextGrid

		return sb.toString();
	}
	
	
	
	
	public static BinaryTier toBinaryTier(LabelTier tier){
		BinaryTier btier = new BinaryTier();
		for(Interval<String> lab : tier){
			Boolean b = true;
			if(lab.label.isEmpty() 
					|| lab.label.startsWith("<") 
					|| lab.label.startsWith("<")  ){
				b = false;
			}
			btier.addInterval(lab.startT, lab.endT, b);
		}
		return btier;
	}
}






