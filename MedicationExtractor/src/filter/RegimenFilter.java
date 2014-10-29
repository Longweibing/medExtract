package filter;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import drugs.DrugEntry;
import text.DischargeDocument;

public class RegimenFilter extends Filter {

	@Override
	public void FilterDrugs(DischargeDocument text) {
		Iterator<DrugEntry> iter = text.getDrugEntries().iterator();
		while (iter.hasNext()) {
			DrugEntry e=iter.next();
			String standardName=e.getName().toLowerCase().trim();
			String[] tokens=standardName.split("\\s");
			for (String s : tokens) {
				if (StringUtils.equalsIgnoreCase(s.trim(), "regimen")) {
					iter.remove();
					break;
				}
			}
			
		}
		
	}

}
