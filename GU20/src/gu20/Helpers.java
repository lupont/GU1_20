package gu20;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;

import gu20.entities.User;

/**
 * Miscellaneous helper methods.
 * @author Pontus Laos
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
	
	/**
	 * Reads a file with a FileReader and returns the lines in a List of Strings.
	 * @param filePath The file to read.
	 * @return A List<String>, each element containing a line of the file.
	 */
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
	
	/**
	 * Reads a log file between the given dates.
	 * @param filePath The log file to read.
	 * @param start The start date.
	 * @param end The end date.
	 * @return A List<String>, each element containing a line of the file.
	 * @throws ParseException 
	 */
	public static final List<String> readLogBetween(String filePath, String start, String end) {
		try (
			FileReader fileReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
		) {
			String line;
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate =  formatter.parse(start);
			Date endDate =  formatter.parse(end);
//			System.out.println(startDate + "\r\n" + endDate);
			
			List<String> lines = new ArrayList<>();
			while ((line = bufferedReader.readLine()) != null) {
				String time = line.substring(line.indexOf('[') + 1, line.indexOf(']'));

				Date t = formatter.parse(time);
				if( (t.after(startDate)||t.equals(startDate))  && (t.before(endDate)||t.equals(endDate))) {
					lines.add(line);
				}
			}

			return lines;
		}
		catch (IOException e) {
			return null;
		}
		catch (ParseException e) {
			System.out.println("Invalid filter input");
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
	
	/**
	 * Takes a generic array and a delimiter, and joins the elements with the delimiter between.
	 * @param input The array.
	 * @param delimiter The delimiter.
	 * @return A String of the elements' toString()-method, with the delimiter between each element.
	 */
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
	
	/**
	 * Prints every client in the object, with some decoration.
	 * @param clients The object containing all the clients to print.
	 */
	public static synchronized void printClients(Clients clients) {
		if (clients.isEmpty()) {
			System.out.println("No clients to print.");
			return;
		}
		
		System.out.println();
		System.out.println("======CLIENTS======");
		Set<Map.Entry<User, Socket>> entrySet = clients.entrySet();
		
		for (Map.Entry<User, Socket> entry : entrySet) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println("===================");
		System.out.println();
	}
	
	/**
	 * Checks which clients in the object are connected to the server.
	 * @param clients The clients to filter.
	 * @return A User[] with all the users belonging to connected clients.
	 */
	public static User[] getConnectedUsers(Clients clients) {
		if (clients.isEmpty()) {
			return new User[0];
		}
		
		List<User> users = new ArrayList<>();
		Set<Map.Entry<User, Socket>> entrySet = clients.entrySet();
		
		for (Map.Entry<User, Socket> entry : entrySet) {
			if (entry.getValue() != null) {
				users.add(entry.getKey());
			}
		}
		
		User[] ret = new User[users.size()];
		ret = users.toArray(ret);
		return ret;
	}
	
	/**
	 * Scales an image to a specified height, width changes dynamically to preserve image ratio
	 * @param srcImg Image to scale
	 * @param height Height (pixels) to scale image to
	 * @return A rescaled image
	 */
	public static Image getScaledImage(Image srcImg, int height) {
		
		if (srcImg == null || height < 1)
			return null;
		
		double ratio = (double) (srcImg.getHeight(null)) / srcImg.getWidth(null);

		int width = (int)(ratio * height);
		
	    BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, width, height, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	/**
	 * Converts an array of MockUser-objects to an array of Strings containing MockUser's usernames.
	 * @param users MockUser-array to convert
	 * @return String array containing MockUser's username
	 */
	public static String[] mockUsersToString(User[] users) {
		if (users != null) {
			String[] strUsers = new String[users.length];
			for (int index = 0; index < users.length; index++) {
				strUsers[index] = users[index].getUsername();
			}
			return strUsers;
		}
		return null;
	}
	
	/**
	 * Converts a file-url to a Image-object
	 * @param file File-object to convert
	 * @return An Image-object of the file
	 * @throws IOException
	 */
	public static Image convertFileToImage(File file) throws IOException {
		if (file == null)
			return null;
		
		Image image = ImageIO.read(file);
		
		return image;		
	}
	
	/**
	 * Custom formatter for the server's log.
	 * @author Pontus Laos
	 *
	 */
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
