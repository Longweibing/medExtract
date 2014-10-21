package medex;

import java.io.File;
import java.util.List;

import drugs.DrugEntry;
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
		int minStart=-1;
		int maxEnd=-1;
		for (String line : medexDocument.split("\n")) {
			DrugEntry e = new DrugEntry();
			e.setD(d);
			line=line.replace("|", " | ");
			String[] attributes=line.split("\\|");
			//get drug name first
			int[] i=getIndices(attributes[1]);
			minStart=i[0];
			maxEnd=i[1];
			e.setName(docText.substring(i[0],i[1]));
			
			//dosage is at 4 OR 5
			i=getIndices(attributes[4]);
			if (i!=null) {
				minStart=Math.min(minStart, i[0]);
				maxEnd=Math.max(maxEnd, i[1]);
				
				e.setDosage(docText.substring(i[0],i[1]));
			} else {
				//if we couldn't find a dosage at 4, go to 5
				i=getIndices(attributes[5]);
				if (i!=null) {
					minStart=Math.min(minStart, i[0]);
					maxEnd=Math.max(maxEnd, i[1]);
					
					e.setDosage(docText.substring(i[0],i[1]));
				}
			}
			
			
			
			//mode is at 6
			i=getIndices(attributes[6]);
			if (i!=null) {
				minStart=Math.min(minStart, i[0]);
				maxEnd=Math.max(maxEnd, i[1]);
				
				e.setMode(docText.substring(i[0],i[1]));
			}
			
			//frequency is at 7
			i=getIndices(attributes[7]);
			if (i!=null) {
				minStart=Math.min(minStart, i[0]);
				maxEnd=Math.max(maxEnd, i[1]);
				
				e.setFreq(docText.substring(i[0],i[1]));
			}
			//duration is at 8
			i=getIndices(attributes[8]);
			if (i!=null) {
				minStart=Math.min(minStart, i[0]);
				maxEnd=Math.max(maxEnd, i[1]);
				
				e.setFreq(docText.substring(i[0],i[1]));
			}
			e.setStartIndex(minStart);
			e.setEndIndex(maxEnd);
			d.addDrugEntry(e);
		}
	}
}
