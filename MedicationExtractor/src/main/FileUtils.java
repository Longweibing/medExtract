package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
	/**
	 * Reads an entire file into memory and returns the contents as a single string
	 * @param filePath The absolute path to the file
	 * @return
	 */
	public static String readFile(String filePath) {
		return readFile(new File(filePath));
	}
	
	/**
	 * Reads an entire file into memory and returns the contents as a single string
	 * @param filePath The absolute path to the file
	 * @return The string or null if there is an error
	 */
	public static String readFile(File f) {
		try {
			StringBuilder sb=new StringBuilder();
			BufferedReader reader=new BufferedReader(new FileReader(f));
			String line=reader.readLine();
			while (line!=null) { //readline will eventually return null when the file is over
				sb.append(line); //add the line we have to the string
				sb.append("\n"); //add the newline character
				line=reader.readLine(); //get the next line
			}
			
			reader.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * Writes the given string out to the given file
	 * @param s
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static void writeFile(String s, File f) throws IOException {
		FileWriter writer=new FileWriter(f);
		writer.write(s);
		writer.close();
	}
	
	/**
	 * Reads the given input stream to a String
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static String readStream(InputStream s) throws IOException {
		InputStreamReader ins = new InputStreamReader(s);
		BufferedReader reader = new BufferedReader(ins);		
	
		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
		    while ((line = reader.readLine()) != null) {
		    	sb.append(line);
			    sb.append("\n");
		    }
			
		    reader.close();
		}
		catch (IOException e) {
		    reader.close();
		}
		return sb.toString();
    }
}
