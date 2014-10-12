package text;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a single line of text
 * @author Eric
 *
 */
public class Line {
	private List<String> line; // holds tokens for this line
	private Section section; // the section that this line is a part of
	public Line(List<String> l, Section s) {
		line=l;
		setSection(s);
	}
	public Line(String str, Section s) {
		setSection(s);
		line=new ArrayList<String>();
		for (String st : str.split("\\w")) {
			line.add(st);
		}
	}
	
	/**
	 * gets the word at the given index
	 * @param i
	 * @return
	 */
	public String getWord(int i) {
		return line.get(i);
	}
	/**
	 * Gets the given number words starting from index i. If not enough words remain, returns null;
	 * @param i
	 * @param number
	 * @return
	 */
	public List<String> getWords(int i, int number) {
		if (i+number>line.size()) {
			return null;
		}
		return line.subList(i, i+number);
	}
	public Section getSection() {
		return section;
	}
	private void setSection(Section section) {
		this.section = section;
	}
}
