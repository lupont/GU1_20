package gu20;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	
	public static <T> String joinArray(T[] input, String delimiter) {
		if (input.length == 0) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length; i++) {
			builder.append(input[i].toString());
			
			if (i != input.length - 1) {
				builder.append(delimiter);
			}
		}
		return builder.toString();
	}
	
	public static synchronized void printClients(Clients clients) {
		if (clients.isEmpty()) {
			System.out.println("No clients to print.");
			return;
		}
		
		System.out.println();
		System.out.println("======CLIENTS======");
		Set<Map.Entry<MockUser, Socket>> entrySet = clients.entrySet();
		
		for (Map.Entry<MockUser, Socket> entry : entrySet) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println("===================");
		System.out.println();
	}
	
	public static synchronized MockUser[] getConnectedUsers(Clients clients) {
		if (clients.isEmpty()) {
			return new MockUser[0];
		}
		
		List<MockUser> users = new ArrayList<>();
		Set<Map.Entry<MockUser, Socket>> entrySet = clients.entrySet();
		
		for (Map.Entry<MockUser, Socket> entry : entrySet) {
			if (entry.getValue() != null) {
				users.add(entry.getKey());
			}
		}
		
		MockUser[] ret = new MockUser[users.size()];
		ret = users.toArray(ret);
		return ret;
	}
}
