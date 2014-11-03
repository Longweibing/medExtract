/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjectExtension;

import java.io.FileWriter;
import au.com.bytecode.opencsv.*;
import java.io.File;
import java.io.IOException;
import text.DischargeDocument;
import java.util.Iterator;
import drugs.DrugEntry;
/**
 *
 * @author fortylashes
 */
public class ProjectExtension {
    //pertinent info assocationed with each record -- fileName will distinguish the records (instance name)
    private String fileName;
    private Integer drugCount= null;
    //want the first 3 drugs related
    private String Drug1 = null;
    private Integer Drug1Index = null;
    private String Drug2 = null;
    private Integer Drug2Index = null;
    private String Drug3 = null;
    private Integer Drug3Index = null;
    //want the first three reason associated with drugs
    private String Reason1 = null;
    private String Reason2 = null;
    private String Reason3 = null;
    
    private String LengthOfStay = null;
    
    public ProjectExtension(){
    }
    
    public void setFileName(String fname){
        this.fileName = fname;
    }
    
    public void setLengthOfStay(Integer days){
        this.LengthOfStay = days.toString();
    }
    
    public String getFileName(){
        return fileName;
    }
    
    public String getDrugCount(){
        return drugCount.toString();
    }
    
    public String getDrug1(){
        return Drug1;
    }
    
    public String getDrug2(){
        return Drug2;
    }
    
    public String getDrug3(){
        return Drug3;
    }
    
    public String getReason1(){
        return Reason1;
    }
    
    public String getReason2(){
        return Reason2;
    }
    
    public String getReason3(){
        return Reason3;
    }
    
    public String getLengthOfStay(){
        return LengthOfStay;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        String delimiter = ",";
        sb.append(this.getFileName());
        sb.append(delimiter);
        sb.append(this.getDrugCount());
        sb.append(delimiter);
        sb.append(this.getDrug1());
        sb.append(delimiter);
        sb.append(this.getDrug2());
        sb.append(delimiter);
        sb.append(this.getDrug3());
        sb.append(delimiter);
        sb.append(this.getReason1());
        sb.append(delimiter);
        sb.append(this.getReason2());
        sb.append(delimiter);
        sb.append(this.getReason3());
        sb.append(delimiter);
        sb.append(this.getLengthOfStay());
        return sb.toString();
    }
    
    public void ProjectExtensionFileCreator(String extFile) throws IOException{
        File projExtOutput = new File(extFile);
        CSVWriter writer = new CSVWriter(new FileWriter(extFile));
        String[] header = "filename,drug_count,drug1,drug2,drug3,reason1,reason2,reason3,length_stay".split(",");
        writer.writeNext(header);
        writer.close();
}
    
    public void ProjectExtensionWriter(String entry, String extFile) throws IOException{
        CSVWriter writer = new CSVWriter(new FileWriter(extFile, true));
        String[] record = entry.split(",");
        writer.writeNext(record);
        writer.close();
    }
    
    public void ProjectExtensionParameterizer(DischargeDocument disDoc){
        //We want to first set the easy parameter -- number of drugs -- by getting the length of the drug list
        this.drugCount = disDoc.getDrugEntries().size();
        
        //Next we want to start getting drugs, starting with those that have reasons
        
        //First specify a counter to count the number of drugs that have been assigned (starting with reasons)
        Integer drugsSpecified = 0;
        Integer indexCounter = -1;//Want to count the indeces so we can keep track of them
        //Next we need to iterate over the drugs in order to get reasons
        for(DrugEntry e: disDoc.getDrugEntries()){
            indexCounter++;
            if(!e.getReason().equals("nm")){
                if(drugsSpecified == 0){
                    drugsSpecified++;
                    this.Drug1 = e.getName();
                    this.Reason1 = e.getReason();
                    this.Drug1Index = indexCounter;
                }
                else if(drugsSpecified == 1){
                    drugsSpecified++;
                    this.Drug2 = e.getName();
                    this.Reason2 = e.getReason();
                    this.Drug2Index = indexCounter;
                }
                else if (drugsSpecified ==2){
                    drugsSpecified++;
                    this.Drug3 = e.getName();
                    this.Reason3 = e.getReason();
                    this.Drug3Index = indexCounter;
                }
                else continue;  
            }//End "if reason == 'nm'"
        }//End iteration over DrugEntrie
        //Now we check to see if we have all of our entries filled out based on reasons, or if we should start gathering non-reason entries from the top of the list
        if(drugsSpecified != 3){
            indexCounter =-1;
            for(DrugEntry e: disDoc.getDrugEntries()){
                indexCounter++;
                if(drugsSpecified == 0){
                    drugsSpecified++;
                    this.Drug1 = e.getName();
                    this.Reason1 = e.getReason();
                    this.Drug1Index = indexCounter;
                }
                else if(drugsSpecified == 1 && this.Drug1Index != indexCounter){
                    drugsSpecified++;
                    this.Drug2 = e.getName();
                    this.Reason2 = e.getReason();
                    this.Drug2Index = indexCounter;
                }
                else if (drugsSpecified ==2 && this.Drug1Index != indexCounter && this.Drug2Index != indexCounter){
                    drugsSpecified++;
                    this.Drug3 = e.getName();
                    this.Reason3 = e.getReason();
                    this.Drug3Index = indexCounter;
                }
            }//End for DrugEntry e loop
        }//If statement
    }//End ProjectExtensionParameterizer
}//End class