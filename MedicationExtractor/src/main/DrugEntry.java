package main;

/**
 * This class represents a DrugEntry, which is a single instance of a drug found in a DischargeDocument.
 * As such a DrugEntry has all the fields necessary to print an entire row of valid output,
 * include name, dosage, freq, indices, and so on.
 * @author Eric
 *
 */

public class DrugEntry {
	private String name=null;
	private String dosage=null;
	private String freq=null;
	private String duration=null;
	private String reason=null;
	private String mode = null;

	private String context=null;
	
	//The absolute indices into the DischargeDocument of this DrugEntry. Indices are generic, meaning they
	//just point to minimal block of text containing all componenets in this doc (as in, within the block,
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
		sb.append(delimiter);
		sb.append("do=\""+this.getDosage()+"\"");
		sb.append(delimiter);
		sb.append("mo=\""+this.getMode()+"\"");
		sb.append(delimiter);
		sb.append("f=\""+this.getFreq()+"\"");
		sb.append(delimiter);
		sb.append("du=\""+this.getDuration()+"\"");
		sb.append(delimiter);
		sb.append("r=\""+this.getReason()+"\"");
		sb.append(delimiter);

		sb.append("ln=\""+this.getContext()+"\"");

		return sb.toString().toLowerCase().replace("\n",  " ");
		
	}
	
	public String getContext() {
		if (context==null || context.isEmpty()) {
			return "narrative"; //guess
		}
		return context;
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
	
}
