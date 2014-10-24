package drugs;

import text.DischargeDocument;
import text.Section;

/**
 * This class represents a DrugEntry, which is a single instance of a drug found in a DischargeDocument.
 * As such a DrugEntry has all the fields necessary to print an entire row of valid output,
 * include name, dosage, freq, indices, and so on.
 * @author Eric
 *
 */

public class DrugEntry {
	//the following strings all correspond to text pulled from the discharge summaries. Strings below
	//should EXACTLY match whatever was pulled from the summaries, including capitalizations, newlines, etc.
	private String name=null;
	private String dosage=null;
	private String freq=null;
	private String duration=null;
	private String reason=null;
	private String mode = null;
	private DischargeDocument d; // the document that this drug is a part of
	private String context=null; //narrative or list
	
	//The absolute indices into the DischargeDocument of this DrugEntry. Indices are generic, meaning they
	//just point to minimal block of text containing all components in this doc (as in, within the block,
	//there may be name, dosage, freq, and so on, in any order).
	private int startIndex;
	private int endIndex;
	
	public DrugEntry() {
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDosage() {
		if (dosage==null || dosage.isEmpty()) {
			return "nm";
		}
		return dosage;
	}
	public void setDosage(String dosage) {
		this.dosage = dosage;
	}
	public String getFreq() {
		if (freq==null || freq.isEmpty()) {
			return "nm";
		}
		return freq;
	}
	public void setFreq(String freq) {
		this.freq = freq;
	}
	public String getDuration() {
		if (duration==null || duration.isEmpty()) {
			return "nm";
		}
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getReason() {
		if (reason==null || reason.isEmpty()) {
			return "nm";
		}
		return reason;
	}
	public void setReason(String reason) {
		
		this.reason = reason;
	}
	public String getMode() {
		if (mode==null || mode.isEmpty()) {
			return "nm";
		}
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
	/**
	 * Returns a string format of this DrugEntry suitable for the challenge
	 */
	
	//TODO: must add offsets to these (assuming they are not nm)
	public String toString() {
		String delimiter="||";
		StringBuilder sb=new StringBuilder();
		sb.append("m=\""+this.getName()+"\"");
		sb.append(getDrugNameOffset());
		sb.append(delimiter);
		sb.append("do=\""+this.getDosage()+"\"");
		sb.append(getDoseOffset());
		sb.append(delimiter);
		sb.append("mo=\""+this.getMode()+"\"");
		sb.append(getModeOffset());

		sb.append(delimiter);
		sb.append("f=\""+this.getFreq()+"\"");
		sb.append(getFreqOffset());

		sb.append(delimiter);
		sb.append("du=\""+this.getDuration()+"\"");
		sb.append(getDurationOffset());

		sb.append(delimiter);
		sb.append("r=\""+this.getReason()+"\"");
		sb.append(getReasonOffset());

		sb.append(delimiter);

		sb.append("ln=\""+this.getContext()+"\"");
	
		return sb.toString().toLowerCase().replace("\n",  " ");
		
	}
	
	public String getContext() {
		if (this.getSection().isList()) {
			return "list";
		}
		return "narrative";
		
	}
	public void setContext(String context) {
		this.context = context;
	}
	public int getStartIndex() {
		return startIndex;
	}
	
	
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	
	/**
	 * Returns the string offset of the given index in the form row:token
	 * @param index
	 * @return
	 */
	public String getFormattedOffset(int row, int token) {
		String offset=""+row+":"+token;
		
		return offset;
	}
	
	
	//TODO: is this method sufficient for getting the correct offset?
	public String getDrugNameOffset() {
		return getOffsetOfString(name);

	}
	
	//returns the absolute index of this drug
	public int getDrugIndex() {
		return getDocument().getText().substring(startIndex,endIndex+1).indexOf(name)+startIndex;

	}
	
	public String getModeOffset() {
		if (getMode().equals("nm")) {
			return ""; //no offset
		}
		return getOffsetOfString(getMode());
	}
	
	public String getFreqOffset() {
		if (getFreq().equals("nm")) {
			return ""; //no offset
		}
		return getOffsetOfString(getFreq());
	}
	
	public String getDoseOffset() {
		if (getDosage().equals("nm")) {
			return ""; //no offset
		}
		return getOffsetOfString(getDosage());
	}
	public String getDurationOffset() {
		if (getDuration().equals("nm")) {
			return ""; //no offset
		}
		return getOffsetOfString(getDuration());
	}
	
	public String getReasonOffset() {
		if (getReason().equals("nm")) {
			return ""; //no offset
		}
		return getOffsetOfString(getReason());
	}
	
	public String getOffsetOfString(String str) {
		int index=getDocument().getText().substring(startIndex,endIndex+1).indexOf(str)+startIndex;
		int endIndex=index+(str.length()-1);
		int row1=getDocument().getRowOfIndex(index);
		int token1=getDocument().getTokenOfIndex(index);
		int row2=getDocument().getRowOfIndex(endIndex);
		int token2=getDocument().getTokenOfIndex(endIndex);
		return " "+getFormattedOffset(row1,token1) + " "+getFormattedOffset(row2,token2);
	}
	public DischargeDocument getDocument() {
		return d;
	}
	
	/**
	 * Returns the section that this drug is a part of
	 * @return
	 */
	public Section getSection() {
		return d.getSection(this.getDrugIndex());
	}
	public void setD(DischargeDocument d) {
		this.d = d;
	}
	
}
