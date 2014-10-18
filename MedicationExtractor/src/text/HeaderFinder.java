package text;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import main.FileUtils;

/**
 * This class contains method for finding section headers in documents
 * @author Eric
 *
 */
public class HeaderFinder {
	
	
	private static void incrementHashMap(HashMap<String,Integer> map, String key) {
		if (!map.containsKey(key)) {
			map.put(key, 1);
		} else {
			map.put(key, 1+map.get(key));
		}
	}
	/**
	 * Returns a set consisting of the headers of every document in the given directories
	 * @param directories
	 * @return
	 */
	public static HashSet<String> findHeaders(List<File> directories) {
		HashMap<String, Integer> headers=new HashMap<String,Integer> ();
		for (File dir : directories) {
			for (File f : dir.listFiles()) {
				String s= FileUtils.readFile(f);
				for (String line : s.split("\n")) {
					try {
						int index=findIndexOfFirstNonCapitalLetter(line);
						if (index<0 && line.length()>1) {
							incrementHashMap(headers,line);
						} else {
							char lastChar=line.charAt(index);
							if (index>=2 && (lastChar==':' || lastChar=='.')) {
								incrementHashMap(headers,line.substring(0,index));
							}
						}
						
					} catch (Exception e) {
						//do nothing
					}
				}
			}
		}
		
		//excludes headers that occur only one time
		HashSet<String> headerSet=new HashSet<String>();
		for (String key : headers.keySet()) {
			if (headers.get(key)>1) {
				headerSet.add(key);
			}
		}
		return headerSet;
	}
	
	public static String findHeaderOfLine(String str) {
		int lastChar=findIndexOfFirstNonCapitalLetter(str);
		if (lastChar!=-1) {
			return str.substring(0,lastChar);

		} else {
			return str;
		}
	}
	
	public static int findIndexOfFirstNonCapitalLetter(String str) {
		for (int i=0;i<str.length();i++) {
			char character=str.charAt(i);
			if ((Character.isLetter(character) && Character.isUpperCase(character)) || (Character.isWhitespace(character))) {
				
			} else {
				return i; // character is not an uppercase letter
			}
		}
		return -1;
	}
	
	
	
}
