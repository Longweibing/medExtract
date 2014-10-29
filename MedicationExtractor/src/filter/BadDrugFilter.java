package filter;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import drugs.DrugEntry;
import main.FileUtils;
import text.DischargeDocument;

public class BadDrugFilter extends Filter {
	
	private static HashSet<String> badDrugs=new HashSet<String>();
	
	public static void compileBadDrugList(File f) {
		String[] drugs=FileUtils.readFile(f).split("\n");
		for (String s : drugs) {
			badDrugs.add(s.toLowerCase().trim());
		}
	}
	
	private static boolean isInteger(String s ){
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void FilterDrugs(DischargeDocument text) {
		Iterator<DrugEntry> iter = text.getDrugEntries().iterator();
		while (iter.hasNext()) {
			DrugEntry e=iter.next();
			String standardName=e.getName().toLowerCase().trim();
			if (badDrugs.contains(standardName)) {
				iter.remove();
				continue;
			} else if (isInteger(standardName)) {
				iter.remove();
				continue;
			} else if (standardName.contains("/")) {
				String[] split=standardName.split("/");
				boolean allInts=true;
				for (String s : split) {
					if (!isInteger(s.trim())) {
						allInts=false;
						break;
					}
				}
				if (allInts) {
					iter.remove();
					continue;
				}
			}
			
			
		}	
		
	}

}
