package merki;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import drugs.DrugEntry;
import text.DischargeDocument;
import text.Section;
import main.FileUtils;
import main.Main;


/**
 * This class is responsible for feeding text into Merki and returning a list of DrugEntries that were found
 * int the text
 * @author Eric
 *
 */

public class MerkiIntegration {
	
	//directory pointing to where MERKI is installed
	private String installDir=null;
	
	//creates a new MerkiIntegration object where the install directory is the given directory
	public MerkiIntegration(String merkiDir) {
		installDir=merkiDir;
	}
	
	
	
	/**
	 * Runs merki on the given discharge document. All drug indices are updated to reflect
	 * the absolute index in the entire document. This function will be augmented to 
	 * do things like exclude bad sections and so on.
	 * @param d
	 * @return
	 */
	public List<DrugEntry> runMerki(DischargeDocument d) {
		List<DrugEntry> entries=new ArrayList<DrugEntry>();
		for (Section s : d.getGoodSections()) {
			List<DrugEntry> newEntries=callMerki(s.getText());
			for (DrugEntry e : newEntries) {
				e.setD(d);
				//adjust indices to be absolute with relation to the document d.
				e.setStartIndex(e.getStartIndex()+s.getStartIndex());
				e.setEndIndex(e.getEndIndex()+s.getStartIndex());

				entries.add(e);
			}
		}
		return entries;
	}
	
	
	/**
	 * Calls MERKI on the given block of text and a list of DrugEntries will every located detail filled in.
	 * A DrugEntry will have a single index-- the absolute index, in characters, of the start of the block
	 * of text this drug entry was found in
	 * @param text
	 * @param tempFile
	 * @return
	 * @throws IOException
	 */
	public List<DrugEntry>  callMerki(String text) {
		//TODO: Might want to choose a better location for this temp file
		
		File tempFile=new File("C:/users/eric/desktop/fakefile.tmp"); //just need a file to write temporary results to.
		try {
			
			//next set of lines just runs MERKI from the command line
			Runtime rt = Runtime.getRuntime();
			
			String[] args=new String[3];
			args[0]="perl";
			args[1]=new File(installDir,"parseFromShell.pl").getAbsolutePath();
			FileUtils.writeFile(text, tempFile);
			args[2]=tempFile.getAbsolutePath();
			Process p = rt.exec(args,null,new File(installDir));
			rt.exec(args, null, new File(installDir)); //run MERKI from within its install directory
			InputStream stream=p.getInputStream();
			
			//MERKI outputs results as an XML document, and this reads that output as a string
			String xmlString=FileUtils.readStream(stream);
			//write the XML to our temp file for parsing
			FileUtils.writeFile(xmlString, tempFile);
			
			
			List<DrugEntry> drugs=getDrugsFromXML(tempFile);
			tempFile.delete();
			return drugs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
		
	}
	/**
	 * Given an element that has a list of sub-elements, each sub element with a name and a 
	 * text value, get the text value of the child with the given name
	 * 
	 * @param e
	 * @param child
	 * @return
	 */
	public static String getStringFromChild(Element e, String child) {
		try {
			return e.getElementsByTagName(child).item(0).getTextContent();

		} catch (Exception error) {
			return "";
		}
	}
	
	/**
	 * Given a <drug> or <possibleDrug> node from MERKI, convert the data in the node to a DrugEntry object
	 * @param n
	 * @return
	 */
	
	public static DrugEntry convertNodeToDrugEntry(Node n, boolean possible) {
		DrugEntry e=new DrugEntry();
		Element element=(Element) n;
		e.setName(getStringFromChild(element,"drugName").trim());
		e.setDosage(getStringFromChild(element,"dose").trim());
		e.setFreq(getStringFromChild(element,"freq").trim());
		e.setMode(getStringFromChild(element,"route").trim());
		
		e.setStartIndex(Integer.parseInt(getStringFromChild(element,"startChar")));
		e.setEndIndex(Integer.parseInt(getStringFromChild(element,"endChar")));

		return e;
	}
	
	/**
	 * Get every Drug found by MERKI and compile them into a list of DrugEntry objects
	 * @param xml
	 * @return
	 */
	public static List<DrugEntry> getDrugsFromXML(File xml) {
		try {
			// Open the config xml file and parse it into a dom			
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();			 			
			Document configDoc = db.parse(xml);
			configDoc.getDocumentElement().normalize();	
			NodeList n=configDoc.getElementsByTagName("drug");
			List<DrugEntry> drugs=new ArrayList<DrugEntry>();
			for (int i=0;i<n.getLength();i++) {
				drugs.add(convertNodeToDrugEntry(n.item(i),false));
			}
			
			//not adding possibleDrug tags, as they seem to very rarely work
			//n=configDoc.getElementsByTagName("possibleDrug");
			//for (int i=0;i<n.getLength();i++) {
			//	drugs.add(convertNodeToDrugEntry(n.item(i),true));
			//}
			
			
			return drugs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
