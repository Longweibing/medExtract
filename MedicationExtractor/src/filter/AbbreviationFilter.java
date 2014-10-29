package filter;

import java.util.Iterator;

import drugs.DrugEntry;
import text.DischargeDocument;

public class AbbreviationFilter extends Filter {
	private static String[] safeList={"c","c."};

	/**
	 * This filter removes drugs with short names that have no other fields set,
	 * as they are likely false positives
	 */
	@Override
	public void FilterDrugs(DischargeDocument text) {
		Iterator<DrugEntry> iter = text.getDrugEntries().iterator();
		while (iter.hasNext()) {
			DrugEntry e=iter.next();
			
			if (e.getName().length()<=3 && FilterManager.countFields(e)==0) {
				boolean include=false;
				for (String s : safeList) {
					if (e.getName().equalsIgnoreCase(s)) {
						//include=true;
						break; //don't exclude
					}
				}
				if (!include) {
					iter.remove();
				}
			}
		}
	}

}
