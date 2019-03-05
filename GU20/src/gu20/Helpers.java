package gu20;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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
	
	/**
	 * Mostly for Linux: gets the first external IPv4 address of the computer.
	 * @return An IPv4 address that other computers can access, null if none is found or if the network interfaces can not be accessed.
	 */
	public static InetAddress getFirstNonLoopbackIPv4Address() {
	    Enumeration<NetworkInterface> networkInterfaces = null;

	    try { 
	    	networkInterfaces = NetworkInterface.getNetworkInterfaces(); 
    	}
	    catch (SocketException ex) { 
	    	return null; 
    	}

	    while (networkInterfaces.hasMoreElements()) {
	        NetworkInterface i = (NetworkInterface) networkInterfaces.nextElement();
	        for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
	            InetAddress address = (InetAddress) en2.nextElement();
	            
	            if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
	            	return address;
	            }
	        }
	    }
	    
	    return null;
	}
}
