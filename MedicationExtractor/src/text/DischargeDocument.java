package text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import drugs.DrugEntry;
import main.FileUtils;


/**
 * An instance of this class represents a single discharge summary. A summary is represented
 * as a list of sections. Instantiation is done by giving this class a string or file containing
 * an entire discharge summary. A breakdown into sections is done internally.
 * @author Eric
 *
 */

public class DischargeDocument {
	private String text;
	private List<Section> sections;
	
	private List<DrugEntry> drugEntries;
	//TODO: Need to be able to find the row/ column given a simple index
	
	
	/**
	 * Create a new DischargeDocument given a string
	 * @param t
	 */
	public DischargeDocument(String t) {
		setText(t);
		sections=new ArrayList<Section>();
		drugEntries=new ArrayList<DrugEntry>();
		
		//breakdown this document into sections using the list of section names contained in the section class
		HashSet<String> sectionNames=Section.getSectionNames();

		int startIndex=0;
		int curLocation=0;
		//iterate through lines, looking for the start of sections
		for (String s : getText().split("\n")) {
			
			//find the 'header' of the current line, with 'header' being defined in findHeaderOfLine
			String header=HeaderFinder.findHeaderOfLine(s);
			if (sectionNames.contains(header)) { //if this is a section header
				if (curLocation!=startIndex) {
					getSections().add(new Section(this,startIndex,curLocation));
					startIndex=curLocation; //start a new window here
				}
			}
			curLocation+=s.length()+1; //+1 to count the removed newline
		}
		//the preceeding loop will not have added the final section
		if (startIndex!=curLocation) {
			getSections().add(new Section(this,startIndex,curLocation));

		}
	}
	
	/**
	 * Create a new DischargeDocument given a file
	 * @param f
	 */
	public DischargeDocument (File f) {
		this(FileUtils.readFile(f));
	}
	
	/**
	 * Returns the entire text
	 */
	
	public String toString() {
		return getText();
	}
	
	/**
	 * Returns the entire text
	 */
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
/**
 * Given an absolute index in characters, returns the 0-indexed row that character is in. 
 * Basically just counts the number of newlines that occurs before the given index
 * @param index
 * @return
 */
	public int getRowOfIndex(int index) {
		return (countChars(text.substring(0,index),'\n')+1);
	}
	
	/**
	 * Gets the token that the given index is a part of. Tokens
	 * are delimited by whitespace and start from 0 on each row.
	 * @param index
	 * @return
	 */
	public int getTokenOfIndex(int index) {
		
		boolean prevWhiteSpace=false; //precondition that index is on a token, so this must be false to start
		int token=0; //count the number of tokens occurring before the current one
		for (int i=index;i>=0;i--) {
			Character c=text.charAt(i);
			if (c=='\n') {
				break; //we have hit the end of this line, so we are done
			}
			if (Character.isWhitespace(c)) {
				if (!prevWhiteSpace) {
					token+=1;
					
				}
				prevWhiteSpace=true; //possible to have multiple spaces, so make sure we don't count each space as a token
			} else {
				prevWhiteSpace=false;
			}
			
		}
		return token;
	}
	/**
	 * Counts occurrences of the given character in the given string
	 * @param str
	 * @param character
	 * @return
	 */
	private static int countChars(String str, char character) {
		int count=0;
		for (char c : str.toCharArray()) {
			if (c==character) {
				count++;
			}
		}
		return count;
	}
	public List<Section> getSections() {
		return sections;
	}
	
	/**
	 * Gets a list of sections that we do want to consider for drugs
	 * @return
	 */
	public List<Section> getGoodSections() {
		List<Section> newSections=new ArrayList<Section>();
		for (Section s : sections) {
			if (!s.doIgnore()) {
				newSections.add(s);
			}
		}
		
		return newSections;
	}

	public List<DrugEntry> getDrugEntries() {
		return drugEntries;
	}
	
	public void addDrugEntry(DrugEntry d) {
		drugEntries.add(d);
	}
	public void addDrugEntries(List<DrugEntry> d) {
		drugEntries.addAll(d);
	}

	public void setDrugEntries(List<DrugEntry> drugEntries) {
		this.drugEntries = drugEntries;
	}
	
	/**
	 * Gets the full output for this document
	 * @return
	 */
	public String getDrugData() {
		StringBuilder sb=new StringBuilder();
		for (DrugEntry d : drugEntries) {
			sb.append(d.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
