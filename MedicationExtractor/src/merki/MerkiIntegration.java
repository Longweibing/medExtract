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

import text.DischargeDocument;
import text.Section;
import main.DrugEntry;
import main.FileUtils;

public class MerkiIntegration {
	private String installDir=null;
	public MerkiIntegration(String merkiDir) {
		installDir=merkiDir;
	}
	
	public List<DrugEntry> runMerki(DischargeDocument d) {
		List<DrugEntry> entries=new ArrayList<DrugEntry>();
		for (Section s : d.getSections()) {
			List<DrugEntry> newEntries=callMerki(s.getText(), new File("C:/users/eric/desktop/testdatafile.txt"));
			for (DrugEntry e : newEntries) {
				//adjust indices to be absolute with relation to the document d.
				e.setStartIndex(e.getStartIndex()+s.getStartIndex());
				e.setEndIndex(e.getEndIndex()+s.getStartIndex());

				System.out.println(e);
				entries.add(e);
			}
		}
		return entries;
	}
	
	
	/**
	 * Calls MERKI on the given block of text and returns the output as a string
	 * @param text
	 * @param tempFile
	 * @return
	 * @throws IOException
	 */
	public List<DrugEntry>  callMerki(String text, File tempFile) {
		try {
			Runtime rt = Runtime.getRuntime();

			String[] args=new String[3];
			args[0]="perl";
			args[1]=new File(installDir,"parseFromShell.pl").getAbsolutePath();
			FileUtils.writeFile(text, tempFile);
			args[2]=tempFile.getAbsolutePath();
			Process p = rt.exec(args,null,new File(installDir));
			rt.exec(args, null, new File(installDir)); //run MERKI from within its install directory
			InputStream stream=p.getInputStream();
			String xmlString=FileUtils.readStream(stream);
			System.out.println(xmlString);
			tempFile.delete();

			FileUtils.writeFile(xmlString, tempFile);
			//System.out.println(FileUtils.readFile(tempFile));
			List<DrugEntry> drugs=getDrugsFromXML(tempFile);
			tempFile.delete();
			return drugs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	public static String getStringFromChild(Element e, String child) {
		try {
			return e.getElementsByTagName(child).item(0).getTextContent();

		} catch (Exception error) {
			return "";
		}
	}
	
	public static DrugEntry convertNodeToDrugEntry(Node n) {
		DrugEntry e=new DrugEntry();
		Element element=(Element) n;
		e.setName(getStringFromChild(element,"drugName"));
		e.setDosage(getStringFromChild(element,"dose"));
		e.setFreq(getStringFromChild(element,"freq"));
		e.setMode(getStringFromChild(element,"route"));
		e.setStartIndex(Integer.parseInt(getStringFromChild(element,"startChar")));
		e.setEndIndex(Integer.parseInt(getStringFromChild(element,"endChar")));

		return e;
	}
	
	public static List<DrugEntry> getDrugsFromXML(File xml) {
		try {
			// Open the config xml file and parse it into a dom			
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();			 			
			Document configDoc = db.parse(xml);
			configDoc.getDocumentElement().normalize();	
			NodeList n=configDoc.getElementsByTagName("drug");
			List<DrugEntry> drugs=new ArrayList<DrugEntry>();
			for (int i=0;i<n.getLength();i++) {
				drugs.add(convertNodeToDrugEntry(n.item(i)));
			}
			
			n=configDoc.getElementsByTagName("possibleDrug");
			for (int i=0;i<n.getLength();i++) {
				drugs.add(convertNodeToDrugEntry(n.item(i)));
			}
			
			
			return drugs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
