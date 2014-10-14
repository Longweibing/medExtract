package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import merki.MerkiIntegration;
import text.DischargeDocument;
import text.HeaderFinder;
import text.Section;

public class Main {
	public static File getResource(String name) {
		
		return new File(Main.class.getResource(name).getFile());
	}		
	public static void main(String[] args) throws IOException {
		
		//just some test code running merki on a file
		Section.compileSections(getResource("/resources/sections.txt")); //read the sections.txt file to get a list of sections
		MerkiIntegration i=new MerkiIntegration(getResource("/merki").getAbsolutePath());

		File testFile=new File("c:/users/eric/desktop/studentdata/studenttrainingfiles/11995.txt");
		DischargeDocument text=new DischargeDocument(testFile);
		for (DrugEntry d : i.runMerki(text)) {
			System.out.println(d);
		}
		
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
