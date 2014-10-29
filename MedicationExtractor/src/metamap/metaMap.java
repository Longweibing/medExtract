/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metamap;

import drugs.DrugEntry;
import gov.nih.nlm.nls.skr.GenericObject;
import java.io.File;
import java.util.List;
import text.DischargeDocument;

/**
 *
 * @author fortylashes
 */
public class metaMap {
    
    public static String runMetaMap(DischargeDocument text, File G){
        GenericObject metaMapObject = new GenericObject("arcadiaperson", "3kjd83kdBn");
        metaMapObject.setField("Email_Address", "michael-lash@uiowa.edu");
                //myGenericObj.setFileField("UpLoad_File", "./sample.txt");
        metaMapObject.setField("Batch_Command", "metamap -E");
                //metaMapObject.setField("BatchNotes", "SKR Web API test");
        metaMapObject.setField("SilentEmail", true);
        
        StringBuilder sb=new StringBuilder();
        for (DrugEntry e: text.getDrugEntries()) {
            sb.append(text.getSurroundingText(e.getStartIndex(), 100));
            sb.append("\n");
	}
        
        List<DrugEntry> drugEntries = text.getDrugEntries();
        
        metaMapObject.setFileField("UpLoad_File", G.getAbsolutePath());
        try{                 
            String results = metaMapObject.handleSubmission();
            System.out.print(results);
            return results;
        }
        catch (RuntimeException ex) {
            
            System.err.println("");
            System.err.print("An ERROR has occurred while processing your");
            System.err.println(" request, please review any");
            System.err.print("lines beginning with \"Error:\" above and the");
            System.err.println(" trace below for indications of");
            System.err.println("what may have gone wrong.");
            System.err.println("");
            System.err.println("Trace:");
            ex.printStackTrace();
        } // catch
        
        return null;
    }
}
