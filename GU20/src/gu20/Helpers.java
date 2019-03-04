package gu20;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Helpers {
	public static final void addFileHandler(Logger logger, String path) {
	    FileHandler fileHandler;  
	    
	    try {  
	        fileHandler = new FileHandler(path);
	        logger.addHandler(fileHandler);
	        
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fileHandler.setFormatter(formatter);  
	    } 
	    catch (SecurityException e) {} 
	    catch (IOException e) {}
	}
}
