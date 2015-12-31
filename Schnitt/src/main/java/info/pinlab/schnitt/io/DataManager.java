package info.pinlab.schnitt.io;

import java.io.IOException;
import java.util.List;

public interface DataManager {

	/**
	 * Reads a .wav or .textgrid file
	 * 
	 * @param file
	 * @return 
	 */
	AudioWithTiers readFile(String file) throws IOException;
	
	
	/**
	 * Parses a directory for .wav and corresponding .textgrid files.
	 * 
	 * @param dir the 
	 * @param isRrecursive  if directories to be checked recursively
	 * @return a list of {@link AudioWithTiers} objects
	 */
	List<AudioWithTiers> parseDir(String dir, boolean isRrecursive);

	/**
	 * Non-recursive directory read.
	 * 
	 * @param dir
	 * @return
	 */
	List<AudioWithTiers> parseDir(String dir);
	
}
