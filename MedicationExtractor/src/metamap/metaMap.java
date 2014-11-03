/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metamap;

import drugs.DrugEntry;
import gov.nih.nlm.nls.skr.GenericObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import main.FileUtils;
import text.DischargeDocument;
/**
 *
 * @author fortylashes
 */
public class metaMap {
    
    public static void runMetaMap(DischargeDocument text, File G) throws IOException{
        GenericObject metaMapObject = new GenericObject("arcadiaperson", "3kjd83kdBn");
        metaMapObject.setField("Email_Address", "michael-lash@uiowa.edu");
                //myGenericObj.setFileField("UpLoad_File", "./sample.txt");
        metaMapObject.setField("Batch_Command", "metamap -J 'dsyn,inpo' -E");
                //metaMapObject.setField("BatchNotes", "SKR Web API test");
        metaMapObject.setField("SilentEmail", true);
        //Construct a stringbuilder to hold all of the reasons
        StringBuilder sb=new StringBuilder();
        for (DrugEntry e: text.getDrugEntries()) {
            String theReason = text.getSurroundingText(e.getStartIndex(), 150);
            String newReason = theReason.replace("\n", " ");
            sb.append(newReason);
            sb.append("\n");
            sb.append("\n");
            //sb.appned(System.getProperty("line.separator"));
	}
        
        //Write the stringbuilder of possible reasons to a file
        String allMedReasons = sb.toString();
        FileUtils.writeFile(allMedReasons, G);
        
        //Send the file to metamap 
        metaMapObject.setFileField("UpLoad_File", G.getAbsolutePath());
        try{                 
            String results = metaMapObject.handleSubmission();
            parseMetaMap(results, text);
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
        
        //return null;
    }
    
    public static void parseMetaMap(String mmResult, DischargeDocument text){
        List<DrugEntry> drugs = text.getDrugEntries();
        String[] individualResults = mmResult.split("Processing 00000000.tx.1:");//This is what each entry is within the DischargeDocument is denoted by
        Integer count = -1;
        for(String s: individualResults){//Now we can iterate and parse out the actual reasons
            count++;
            DrugEntry curDrug = drugs.get(count);
            //ArrayList<String> reasons = new ArrayList();
            //System.out.println(s);
            String[] subRes = s.split("Processing 00000000.tx.");
            for(String subS: subRes){
                String[] phraseRes = subS.split("Phrase:");
                for(String returnedResults: phraseRes){
                    if(returnedResults.contains("Meta Mapping")){
                        Integer reasonFoundIndex = -1;
                        //System.out.println(returnedResults);
                        String[] reasonFound = returnedResults.split("Meta Mapping");
                        String actualText = reasonFound[0];
                        //System.out.println(actualText);
                        String metaMapText = reasonFound[1];
                        String[] metaMapSplit = metaMapText.split("  ");
                        String actualMetaMap = metaMapSplit[2];//this is the text that returned the reason
                        String[] splitActualMetaMap = actualMetaMap.split(" ");
                        String firstItem = splitActualMetaMap[1];//first item in the metamap given reason
                        String lastItem = null;
                        if(firstItem.length() > 3){//only want to get reasons if the length is greate than 3 -- false positives
                            Integer lastItemIndex = -1;
                            for(String lastItemDetect: splitActualMetaMap){//if there is more than one word we need make sure to get all of the words from the actual text
                                lastItemIndex++;
                                if(lastItemDetect.contains("(") || lastItemDetect.contains("[")){
                                    lastItem = splitActualMetaMap[lastItemIndex-1];
                                    break;
                                }
                            }
                            String[] actualTextSplit = actualText.split(" ");
                            String lowerFirstItem = firstItem.toLowerCase();
                            if(firstItem.equals(lastItem)){
                                for(String actual: actualTextSplit){
                                    if(lowerFirstItem.equals(actual.toLowerCase())){
                                        //reasons.add(actual);
                                        if(curDrug.getReason().length() < actual.length()){
                                            curDrug.setReason(actual);
                                        }
                                        break;
                                    }
                                }
                            }
                            else{
                                Integer newEnding = -1;
                                for(String blah: actualTextSplit){
                                    newEnding++;
                                    if(blah.equals(lastItem)) break;
                                }
                                StringBuilder multiWordReason = new StringBuilder();
                                String multiWordDelimiter = " ";
                                Integer multiWordIndexCount = -1;
                                for(String actual: actualTextSplit){
                                    multiWordIndexCount++;
                                    if(lowerFirstItem.equals(actual.toLowerCase())){
                                        for(int starting=multiWordIndexCount; starting<newEnding; starting++){
                                            System.out.println("Current word is: "+actualTextSplit[starting]);
                                            multiWordReason.append(actualTextSplit[starting]);
                                            multiWordReason.append(multiWordDelimiter);
                                        }
                                        multiWordReason.append(actualTextSplit[newEnding]);
                                        String stringToAdd = multiWordReason.toString().replace(System.getProperty("line.separator"), "").replace(".", "");
                                        //reasons.add(stringToAdd);
                                        if(curDrug.getReason().length() < stringToAdd.length()){
                                            curDrug.setReason(stringToAdd);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        /*for(String item: metaMapSplit){
                            reasonFoundIndex++;
                            System.out.println("Index number: "+reasonFoundIndex.toString());
                            System.out.println(item);
                        for(String reason: reasonFound){
                            reasonFoundIndex++;
                            System.out.println("Index number: "+reasonFoundIndex.toString());
                            System.out.println(reason);
                        }*/
                    }
                }
                
            }
            
            
            
            
        //System.out.println(text.getDrugEntries().size());
        }
    }
    
}
