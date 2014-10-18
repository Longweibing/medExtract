package text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import main.FileUtils;

public class Section {
	//TODO: populate this hashmap with names + types
	
	private static HashSet<String> sectionNames=new HashSet<String>();
	
	private static HashMap<String, String> nameToType=new HashMap<String,String>(); //maps section names to types
	
	//contains a list of section headers that should not be parsed for medications
	private static HashSet<String> namesToIgnore=new HashSet<String>();
	
	
	public static void compileSections(File sectionFile, File badSectionFile) {
		for (String s : FileUtils.readFile(sectionFile).split("\n")) {
			getSectionNames().add(s);
		}
		
		for (String s : FileUtils.readFile(badSectionFile).split("\n")) {
			namesToIgnore.add(s);
		}
	}
	
	private int startIndex; //index in the actual, original document where this section begins
	private int endIndex;   //index+1 of final character in this section
	private String name = null; //things like "allergy", "assessment", etc.
	private List<Line> lines=null; // text broken down into a list of lines;
	private DischargeDocument doc=null;
	/**
	 * builds a new section from the given text
	 * @param t
	 */
	public Section(DischargeDocument d, int s, int e) {
		
		setDoc(d);
		setStartIndex(s);
		setEndIndex(e);
		name=HeaderFinder.findHeaderOfLine(this.getText().split("\n")[0]); //the name of this header
		String[] lns=this.getText().split("\n");
		lines=new ArrayList<Line>();
		for (String str : lns) {
			lines.add(new Line(str,this));
		}
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	/**
	 * returns a line using a local index (as in, the first line in this section is 0)
	 * @param i
	 * @return
	 */
	public Line getLineLocal(int i) {
		return lines.get(i);
	}
	
	
	
	public String getType() {
		return Section.nameToType.get(this.name);
	}
	
	
	/**
	 * Returns true if this section should be ignored
	 * @return
	 */
	public boolean doIgnore() {
		return Section.namesToIgnore.contains(this.name);
	}

	public static HashSet<String> getSectionNames() {
		return sectionNames;
	}

	public static void setSectionNames(HashSet<String> sectionNames) {
		Section.sectionNames = sectionNames;
	}
	
	public void addLine(String text) {
		lines.add(new Line(text,this));
	}
	
	public String toString() {
		return this.getText();
	}
	
	public String getText() {
	
		return getDoc().getText().substring(getStartIndex(),getEndIndex());
	}
	
	/**
	 * Absolute index of DischargeDocument where this section begins.
	 * @return
	 */
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public DischargeDocument getDoc() {
		return doc;
	}
	public void setDoc(DischargeDocument doc) {
		this.doc = doc;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
}
