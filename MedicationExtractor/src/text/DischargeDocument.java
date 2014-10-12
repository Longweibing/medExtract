package text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import main.FileUtils;

public class DischargeDocument {
	private String text;
	private List<Section> sections;
	
	//TODO: Need to be able to find the row/ column given a simple index
	
	public DischargeDocument(String t) {
		setText(t);
		setSections(new ArrayList<Section>());
		HashSet<String> sectionNames=Section.getSectionNames();

		int startIndex=0;
		int curLocation=0;
		for (String s : getText().split("\n")) {
			
			String header=HeaderFinder.findHeaderOfLine(s);
			if (sectionNames.contains(header)) {
				if (curLocation!=startIndex) {
					getSections().add(new Section(this,startIndex,curLocation));
					startIndex=curLocation;
				}
			}
			curLocation+=s.length()+1; //+1 to count the removed newline
		}
	}
	public DischargeDocument (File f) {
		this(FileUtils.readFile(f));
	}
	
	public String toString() {
		return getText();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	//returns the row of text this index is in (0 indexed rows)
	public int getRowOfIndex(int index) {
		return (countChars(text.substring(0,index),'\n'));
	}
	
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
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
}
