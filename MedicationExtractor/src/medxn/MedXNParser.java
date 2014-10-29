package medxn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import drugs.DrugEntry;
import drugs.DrugUtils;
import main.FileUtils;
import text.DischargeDocument;

public class MedXNParser {

	private static void printArr(int[] arr) {
		if (arr==null) {
			return;
		}
	}
	private static String getNextFileName(String medexAttr) {
		return medexAttr.split("\n")[1].trim();
	}
	/**
	 * Given a MedXN attribute, which looks like colace::294::300 or something similar,
	 * returns the start and end indices
	 * @param medexAttr
	 * @return
	 */
	private static int[] getIndices(String medexAttr) {

		if (medexAttr==null || medexAttr.trim().isEmpty()) {
			return null;
		}
		int[] indices=new int[2];
		int firstIndex=medexAttr.indexOf("::")+2;
		int currentIndex=firstIndex;
		while (true) {
			currentIndex++;
			if (medexAttr.charAt(currentIndex)==':') {
				indices[0]=Integer.parseInt(medexAttr.substring(firstIndex,currentIndex));
				break;
			}
		}
		firstIndex=currentIndex+2; //character after the ::
		if (medexAttr.substring(firstIndex).contains("`")) {
			indices[1]=Integer.parseInt(medexAttr.substring(firstIndex,firstIndex+medexAttr.substring(firstIndex).indexOf("`")).trim());

		} else {
			indices[1]=Integer.parseInt(medexAttr.substring(firstIndex).trim());

		}

		
		return indices;
	}
	//filename|medication::b::e|medication RxCUI|strength::b::e|dose::b::e|form::b::e|route::b::e|frequency::b::e|duration::b::e|specific RxNorm name|specific RxCUI|sentence
	public static void parseMedXNResults(DischargeDocument d, File output) {
		String outString=FileUtils.readFile(output);
		outString=outString.replace("@@@", " @@@ ");
		String[] attrs=outString.split("@@@");
		int index=0;
		String nextFileName=attrs[0].trim();
		while (index<attrs.length) {
			if (nextFileName.equals(d.getFileName())) {
				String docText=d.getText();
				DrugEntry e = new DrugEntry();
				e.setD(d);
				
				int[] i=getIndices(attrs[index+1]);
				e.setName(docText.substring(i[0],i[1]));
				e.setStartIndex(i[0]);
				e.setEndIndex(i[1]);
				
				
				
				
				//mode is at 6
				i=getIndices(attrs[index+6]);
				printArr(i);

				if (i!=null) {
					
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setMode(docText.substring(i[0],i[1]));
				}
				
				//frequency is at 7
				i=getIndices(attrs[index+7]);

				if (i!=null) {
					
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setFreq(docText.substring(i[0],i[1]));
				}
				//duration is at 8
				i=getIndices(attrs[index+8]);

				if (i!=null) {
					
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setDuration(docText.substring(i[0],i[1]));
				}
				
				//dosage is at 3 or 4
				i=getIndices(attrs[index+3]);

				if (i!=null) {
						
						//first, set the first dosage for the fist drug
						e.setStartIndex(Math.min(e.getStartIndex(), i[0]));
						e.setEndIndex(Math.max(e.getEndIndex(), i[1]));
						e.setDosage(docText.substring(i[0],i[1]));
						e.setDosageStartIndex(i[0]);
					
					
				}
				
				
				
				d.addDrugEntry(e);
				
				
			}

			index+=11;
			if (index>=attrs.length-1) {
				break; //done
			}
			//System.out.println(attrs[index]);
			nextFileName=getNextFileName(attrs[index]);
			
		}
	}
}
