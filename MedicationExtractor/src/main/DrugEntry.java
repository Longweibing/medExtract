package main;

import text.DischargeDocument;
import text.Section;

public class DrugEntry {
	private String name=null;
	private String dosage=null;
	private String freq=null;
	private String duration=null;
	private String reason=null;
	private String mode = null;
	private String event=null;
	private String temporal=null;
	private String certainty=null;
	private String context=null;
	
	//TODO: refactor startIndex and endIndex to be absolute
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
		sb.append("e=\""+this.getEvent()+"\"");
		sb.append(delimiter);

		sb.append("t=\""+this.getTemporal()+"\"");
		sb.append(delimiter);
		sb.append("c=\""+this.getCertainty()+"\"");
		sb.append(delimiter);
		sb.append("ln=\""+this.getContext()+"\"");

		return sb.toString().toLowerCase().replace("\n",  " ");
		
	}
	public String getEvent() {
		if (event==null || event.isEmpty()) {
			return "nm";
		}
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getTemporal() {
		if (temporal==null || temporal.isEmpty()) {
			return "nm";
		}
		return temporal;
	}
	public void setTemporal(String temporal) {
		this.temporal = temporal;
	}
	public String getCertainty() {
		if (certainty==null || certainty.isEmpty()) {
			return "nm";
		}
		return certainty;
	}
	public void setCertainty(String certainty) {
		
		this.certainty = certainty;
	}
	public String getContext() {
		if (certainty==null || certainty.isEmpty()) {
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
