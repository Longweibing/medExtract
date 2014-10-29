package main;

import i2b2.i2b2Integration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.medex.MedEx;
import org.apache.medex.MedTagger;
import org.apache.uima.resource.ResourceInitializationException;

import drugs.DrugEntry;
import drugs.DrugUtils;
import filter.FilterManager;
import medex.MedexResultsParser;
import medxn.MedXNParser;
import merki.MerkiIntegration;
import text.DischargeDocument;
import text.HeaderFinder;
import text.Section;
//import gov.nih.nlm.nls.skr.*
import gov.nih.nlm.nls.skr.*;
import java.util.Iterator;


public class Main {
	public static File getResource(String name) {
		
		return new File(Main.class.getResource(name).getFile());
	}		
	
	/**
	 * This function is the main entry point to the program. It takes in a directory for input,
	 * which can have many different discharge summaries. It finds the medication info in each file
	 * and outputs it to the outputDirectory. After execution, the outputDirectory should contain
	 * one file of output for each discharge summary.
	 * @param inputDirectory
	 * @param outputDirectory
	 * @throws IOException 
	 * @throws ResourceInitializationException 
	 */
	
	/*
	 * We take the following steps
	 * 1) Run MedEx on every file in the directory. Results are used in later steps
	 * 2) Iterate through every document, identifying DrugEntries.
	 * 		a) Create a DischargeDocument object
	 * 	 	b) Read in MedEx information for a document
	 * 		c) Supplement MedEx info with MERKI info
	 * 		d) Filter out duplicates between MedEx and MERKI
	 * 		e) Add reason information from MetaMap
	 * 		f) Filter out DrugEntries based on common sources of false-positives
	 * 		g) For each document, write out results to the output directory
	 */
	private static void getResults(File inputDirectory, File outputDirectory) throws IOException, ResourceInitializationException {
		//reads in section lexicons and stores them in memory for the duration of the execution
		
		MedXNParser.runMedXN();

		
		Section.compileSections(getResource("/resources/sections.txt"),getResource("/resources/badSections.txt"),getResource("/resources/listSections.txt")); //read the sections.txt file to get a list of sections
		File goldStandard=getResource("/resources/StudentData/goldStandards/gold.xml");
		File records=getResource("/resources/StudentData/studentTrainingFiles");
		File medexOutput=new File(outputDirectory, "medex");
		File finalOutput=new File(outputDirectory, "results");
		finalOutput.mkdirs();
		medexOutput.mkdirs();
		FilterManager.loadFilters();
		i2b2Integration i2b2=new i2b2Integration(getResource("/i2b2").getAbsolutePath(),
				goldStandard.getAbsolutePath(),records.getAbsolutePath(), finalOutput.getAbsolutePath());
		
		MerkiIntegration i=new MerkiIntegration(getResource("/merki").getAbsolutePath()); 
		

		//step one above
		MedTagger m=MedEx.getMedTagger(inputDirectory.getAbsolutePath(), medexOutput.getAbsolutePath());
		m.run_batch_medtag();
                
                //making new 
		GenericObject metaMapObject = new GenericObject("arcadiaperson", "3kjd83kdBn");
                metaMapObject.setField("Email_Address", "michael-lash@uiowa.edu");
                //myGenericObj.setFileField("UpLoad_File", "./sample.txt");
                metaMapObject.setField("Batch_Command", "metamap -E");
                //metaMapObject.setField("BatchNotes", "SKR Web API test");
                metaMapObject.setField("SilentEmail", true);
                //metaMapObject.setFileField("UpLoad_File", "/Users/fortylashes/Documents/Health_Data_Analytics/Project2/examples/sample.txt");
                String res = metaMapObject.handleSubmission();
                System.out.println(res);
                
                
		//store all documents so we can output them at the end
		List<DischargeDocument> docs=new ArrayList<DischargeDocument>();
		for (File f : inputDirectory.listFiles()) {
                        if (f.getName().startsWith(".")) {
                            continue;
                        }
                        System.out.println(f);
			DischargeDocument text=new DischargeDocument(f);
			docs.add(text);
			
			//part b above
			System.out.println("parsing MedEx results");
			MedexResultsParser.parseMedexResults(text, new File(medexOutput,f.getName()));
			//part c above. Adds all drugs MERKI can find to this element
			System.out.println("running MERKI");
			//i.runMerki(text);
			
			//filter out duplicates
			System.out.println("filtering out duplicates");
			//DrugUtils.filterDuplicateDrugs(text);
			//TODO: Down here, we will want a function that takes a DischargeDocument that 
			//already has medications loaded into it and adds as many reasons as possible. (MetaMap)
			
                        //part e above. Using the define metamap "genericObject" to send a request
                        //
                        //
                        //(1)Get the list of drugs
                        List<DrugEntry> drugEntries = text.getDrugEntries();
                        File G = new File(outputDirectory, "metaMapTemp.txt");
                
                        //(2)Iterate over the list of drugs
                        Iterator<DrugEntry> drugEntIt = drugEntries.iterator();
                        while(drugEntIt.hasNext()){
                            //Get a handle on the object
                            DrugEntry drugObj = drugEntIt.next();
                            //Get the appropriate surrounding text
                            String reasonTxt = text.getSurroundingText(drugObj.getStartIndex(), 100);
                            FileUtils.writeFile(reasonTxt, G);
                            //Set the appropriate field to the text containing th reason
                            //System.out.println(G.getAbsolutePath());
                            metaMapObject.setFileField("UpLoad_File", G.getAbsolutePath());
                            //System.out.println(reasonTxt);
                            //metaMapObject.setField("APIText", reasonTxt);
                            //Send this to the metamap web api
                            try{
                               
                                String results = metaMapObject.handleSubmission();
                                System.out.print(results);

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
                        }//Drug Iterator
			
			//step f above. New filters can be defined in the filter package
			System.out.println("filtering out false positives");
			FilterManager.runAllFilters(text);
			
			System.out.println("printing out results");
			System.out.println(f.getName());
			
			File outputFile=new File(finalOutput,(f.getName().replace(".txt", ""))+".i2b2.entries");
			
			FileUtils.writeFile(text.getDrugData(), outputFile);
			
			
			
		}
		
		i2b2.printResults();
		
	}
	
	

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		getResults(getResource("/resources/StudentData/studentTrainingFiles"), 
				new File(getResource("/resources/StudentData"),"output"));

		/*
		//just some test code running merki on a file
		Section.compileSections(getResource("/resources/sections.txt"),getResource("/resources/badSections.txt")); //read the sections.txt file to get a list of sections
		
		File trainingDir=getResource("/resources/StudentData/studentTrainingFiles");
		
		File outputDirectory=new File("C:/users/eric/desktop/healthOutput");
		MerkiIntegration i=new MerkiIntegration(getResource("/merki").getAbsolutePath());

		for (File f : trainingDir.listFiles()) {
			//this is a temp file
			if (f.getName().startsWith("._")) {
				continue;
			}
			File outputFile=new File(outputDirectory,"output_"+f.getName());
			System.out.println(outputFile.getAbsolutePath());
			FileWriter writer=new FileWriter(outputFile);
			DischargeDocument text=new DischargeDocument(f);
			System.out.println("running MERKI on "+f.getName());
			for (DrugEntry d : i.runMerki(text)) {
				writer.write(d.toString());
				writer.write("\n");
			}
			writer.close();
		}
		*/

		
		
		//the code below just compiles section names. It is no longer needed for the project.
		/*List<File> dirs=new ArrayList<File> ();
		dirs.add(new File("c:/users/eric/desktop/studentdata/studenttrainingfiles"));
		dirs.add(new File("c:/users/eric/desktop/studentdata/AdditionalDischarges-set4"));

		HashSet<String> mySet=HeaderFinder.findHeaders(dirs);
		List<String> names=new ArrayList<String>();

		for (String s : mySet) {
			names.add(s);
		}
		Collections.sort(names);
		
		for (String s : names) {
			System.out.println(s);
		}
		try {
			FileWriter writer=new FileWriter(new File("c:/users/eric/desktop/sections.txt"));
			for (String s : names) {
				writer.write(s);
				writer.write("\n");
				
			}
			writer.close();
		} catch (Exception e) {
			
		}*/
	}
	

}
