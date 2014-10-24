package filter;

import java.util.Iterator;

import drugs.DrugEntry;
import text.DischargeDocument;

public class SectionFilter extends Filter {

	@Override
	public void FilterDrugs(DischargeDocument text) {
		Iterator<DrugEntry> iter = text.getDrugEntries().iterator();
		while (iter.hasNext()) {
			DrugEntry e=iter.next();
			if (e.getSection().doIgnore()) {
				iter.remove();
			}
			
		}
		
	}

}
