package medex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import drugs.DrugEntry;
import drugs.DrugUtils;
import main.FileUtils;
import text.DischargeDocument;

public class MedexResultsParser {
	
	/**
	 * Given a MedEx attribute, which looks like colace[294,300] or something similar,
	 * returns the start and end indices
	 * @param medexAttr
	 * @return
	 */
	private static int[] getIndices(String medexAttr) {
		if (medexAttr==null || medexAttr.trim().isEmpty()) {
			return null;
		}
		//System.out.println(medexAttr);
		int[] indices=new int[2];
		int firstIndex=medexAttr.indexOf("[")+1;
		int currentIndex=firstIndex;
		while (true) {
			currentIndex++;
			if (medexAttr.charAt(currentIndex)==',') {
				indices[0]=Integer.parseInt(medexAttr.substring(firstIndex,currentIndex));
				break;
			}
		}
		firstIndex=currentIndex+1; //character after the comma
		currentIndex=firstIndex;
		while (true) {
			currentIndex++;
			if (medexAttr.charAt(currentIndex)==']') {
				indices[1]=Integer.parseInt(medexAttr.substring(firstIndex,currentIndex));
				break;
			}
		}
		return indices;
	}
	/**
	 * This function adds all the DrugEntries identified by MedEx to the given 
	 * DischargeDocument
	 * @param d
	 * @param outputFile
	 */
	public static void parseMedexResults(DischargeDocument d, File outputFile) {
		String medexDocument=FileUtils.readFile(outputFile);
		String docText=d.getText();
		//the medex format is to include a single drug on each line

		for (String line : medexDocument.split("\n")) {
			try {
				List<DrugEntry> drugsInLine=new ArrayList<DrugEntry>();
				drugsInLine.add(new DrugEntry());
				drugsInLine.get(0).setD(d);
				line=line.replace("|", " | ");
				String[] attributes=line.split("\\|");
				//get drug name first
				int[] i=getIndices(attributes[1]);
				drugsInLine.get(0).setStartIndex(i[0]);
				drugsInLine.get(0).setEndIndex(i[1]);
				for (DrugEntry e: drugsInLine) {
					e.setName(docText.substring(i[0],i[1]));

				}
				
				//dosage is at 4 AND/OR 5
				i=getIndices(attributes[4]);
				//if we couldn't find a dosage at 4, go to 5
				int[] i2=getIndices(attributes[5]);
				if (i!=null && i2!=null) {
					//if there are two dosages, we need to have two different drugs to represent this
					List<DrugEntry> copies=new ArrayList<DrugEntry>();
					for (DrugEntry e: drugsInLine) {
						DrugEntry copy=DrugUtils.copyDrug(e);
						copies.add(copy);
						//first, set the first dosage for the fist drug
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setDosage(docText.substring(i[0],i[1]));
						e.setDosageStartIndex(i[0]);
						
						//now, add the second dosage to the new copy drug
						copy.setStartIndex(Math.min(copy.getStartIndex(), i2[0]));
						copy.setEndIndex(Math.max(copy.getEndIndex(), i2[1]));
						copy.setDosage(docText.substring(i2[0],i2[1]));
						copy.setDosageStartIndex(i2[0]);
					}
					drugsInLine.addAll(copies);
					
				} else if (i!=null) {
					for (DrugEntry e: drugsInLine) {
						
						//first, set the first dosage for the fist drug
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setDosage(docText.substring(i[0],i[1]));
						e.setDosageStartIndex(i[0]);
						
					}
				} else if (i2!=null) {
					for (DrugEntry e: drugsInLine) {
						
						//first, set the first dosage for the fist drug
						e.setStartIndex(Math.min(e.getStartIndex(), i2[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i2[1]));
						e.setDosage(docText.substring(i2[0],i2[1]));
						e.setDosageStartIndex(i2[0]);
					
					}
				}
				
				//mode is at 6
				i=getIndices(attributes[6]);
				if (i!=null) {
					
					for (DrugEntry e : drugsInLine) {
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setMode(docText.substring(i[0],i[1]));

					}
				}
				
				//frequency is at 7
				i=getIndices(attributes[7]);
				if (i!=null) {
					
					for (DrugEntry e : drugsInLine) {
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setFreq(docText.substring(i[0],i[1]));

					}
				}
				//duration is at 8
				i=getIndices(attributes[8]);
				if (i!=null) {
					
					for (DrugEntry e : drugsInLine) {
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setDuration(docText.substring(i[0],i[1]));

					}
				}
				for (DrugEntry e : drugsInLine) {
					
					d.addDrugEntry(e);
				}
			} catch (Exception e) {
				//just move on
			}
			
			
		}
	}
}
