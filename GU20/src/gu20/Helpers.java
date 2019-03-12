package gu20;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
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
	        fileHandler.setFormatter(new LogFormatter());
	        logger.addHandler(fileHandler);
	    } 
	    catch (SecurityException e) {} 
	    catch (IOException e) {}
	}
	
	public static final List<String> readFile(String filePath) {
		try (
			FileReader fileReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
		) {
			String line;
			List<String> lines = new ArrayList<>();
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
			
			return lines;
		}
		catch (IOException ex) {
			return null;
		}
	}
	
	public static final List<String> readLogBetween(String filePath, String start, String end) {
		try (
			FileReader fileReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
		) {
			String line;
			List<String> lines = new ArrayList<>();
			while ((line = bufferedReader.readLine()) != null) {
				String time = line.substring(line.indexOf('[') + 1, line.indexOf(']'));
				
				if (start.compareTo(time) < 0 && end.compareTo(time) >= 0) {
					lines.add(line);
				}
			}
			
			return lines;
		}
		catch (IOException ex) {
			return null;
		}
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
	
	static class LogFormatter extends Formatter {
		// TODO: change to DateTimeFormatter.
		private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

		@Override
	    public String format(LogRecord record) {
	        StringBuilder builder = new StringBuilder(1000);
	        
	        // Log the date and time in the specified format
	        builder.append("[").append(dateFormat.format(new Date(record.getMillis()))).append("] - ");

	        // Log the level
	        builder.append("[").append(record.getLevel()).append("] ");

	        // Log the info
	        builder.append(formatMessage(record));

	        // Add a new line at the end
	        builder.append(System.lineSeparator());

	        return builder.toString();
	    }
	}
}
