package info.pinlab.schnitt.io;

import java.io.File;
import java.util.List;

public interface DataManager {

	/**
	 * Reads a .wav or .textgrid file
	 * 
	 * @param file
	 * @return 
	 */
	AudioWithTiers readFile(File file);
	
	
	/**
	 * Parses a directory for .wav and corresponding .textgrid files.
	 * 
	 * @param dir the 
	 * @param isRrecursive  if directories to be checked recursively
	 * @return a list of {@link AudioWithTiers} objects
	 */
	List<AudioWithTiers> parseDir(File dir, boolean isRrecursive);

	
}
