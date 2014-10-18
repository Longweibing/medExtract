package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.medex.MedEx;
import org.apache.medex.MedTagger;

import drugs.DrugEntry;
import merki.MerkiIntegration;
import text.DischargeDocument;
import text.HeaderFinder;
import text.Section;

public class Main {
	public static File getResource(String name) {
		
		return new File(Main.class.getResource(name).getFile());
	}		
	public static void main(String[] args) throws IOException {
		MedTagger m=MedEx.getMedTagger("C:/users/eric/desktop/studentdata/studenttrainingfiles", "C:/users/eric/desktop/healthoutput");
		m.run_batch_medtag();
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
