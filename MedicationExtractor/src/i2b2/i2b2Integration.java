package i2b2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import main.FileUtils;
import drugs.DrugEntry;

public class i2b2Integration {
	
	
	private File homeDirectory;
	private File goldXML;
	private File recordsDir;
	private File resultsDir;
	
	/**
	 * Creates an object that will make it possible to get i2b2 data using the 
	 * @param progPath Directory containing i2b2eval.py and full installation
	 * @param goldPath Path to gold.xml file for student training files
	 * @param dataPath Path to student training files
	 * @param resultsPath Path to our results
	 */
	public i2b2Integration(String progPath, String goldPath, String dataPath, String resultsPath) {
		homeDirectory=new File(progPath);
		goldXML=new File(goldPath);
		recordsDir=new File(dataPath);
		resultsDir=new File(resultsPath);
	}
	
	private void printArr(String[] arr) {
		for (String s: arr) {
			System.out.print(s+" ");
			
		}
		System.out.println();
	}
	
	public void printResults() {
		
			try {
				File testXML=new File(homeDirectory,"curTest.xml");
				//next set of lines just runs MERKI from the command line
				Runtime rt = Runtime.getRuntime();
				String[] args=new String[9];
				args[0]="C:\\Python27\\python.exe";
				args[1]=new File(homeDirectory,"i2b2eval.py").getAbsolutePath();
				args[2]="-x";
				args[3]="-r";
				args[4]=recordsDir.getAbsolutePath()+File.separator;
				args[5]="-z";
				args[6]=resultsDir.getAbsolutePath()+File.separator;
				args[7]="-o";
				args[8]=testXML.getAbsolutePath();
				printArr(args);
				Process p = rt.exec(args,null,homeDirectory);
				InputStream stream=p.getInputStream();
				
				System.out.println(FileUtils.readStream(stream));
				args=new String[6];
				
				args[0]="C:\\Python27\\python.exe";
				args[1]=new File(homeDirectory,"i2b2eval.py").getAbsolutePath();
				args[2]="-g";
				args[3]=goldXML.getAbsolutePath();
				args[4]="-s";
				args[5]=testXML.getAbsolutePath();
				printArr(args);
				p = rt.exec(args,null,homeDirectory);
				stream=p.getInputStream();
				
				System.out.println(FileUtils.readStream(stream));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
}
