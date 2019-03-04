package gu20;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Miscellaneous helper methods.
 * @author lupont
 *
 */
public final class Helpers {
	
	/**
	 * Attaches a FileHandler to the given Logger, making it log to the specified path.
	 * @param logger The Logger that should get the FileHandler.
	 * @param path The path to the log file.
	 */
	public static final void addFileHandler(Logger logger, String path) {
	    FileHandler fileHandler;  
	    
	    try {  
	        fileHandler = new FileHandler(path, true);
	        logger.addHandler(fileHandler);
	        
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fileHandler.setFormatter(formatter);  
	    } 
	    catch (SecurityException e) {} 
	    catch (IOException e) {}
	}
}
