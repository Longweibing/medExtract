package filter;

import java.util.Iterator;

import drugs.DrugEntry;
import text.DischargeDocument;

public class AbbreviationFilter extends Filter {

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
				iter.remove();
			}
		}
	}

}
