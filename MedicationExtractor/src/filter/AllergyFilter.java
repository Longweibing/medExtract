package filter;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import drugs.DrugEntry;
import text.DischargeDocument;

/**
 * This filter attempts to remove as many drugs as possible that are actually
 * allergy indications
 */

public class AllergyFilter extends Filter {
	String[] proximityTriggers={"allergic", "allergy"};
	String[] badPhrases={"allergic to", "allergy to"};
	@Override
	public void FilterDrugs(DischargeDocument text) {

		Iterator<DrugEntry> iter = text.getDrugEntries().iterator();
		while (iter.hasNext()) {
			DrugEntry e=iter.next();
			boolean removed=false;
			String context=text.getSurroundingText(e.getDrugIndex(), 60);
			for (String phrase : badPhrases) {
				if (StringUtils.containsIgnoreCase(context, phrase)) {
					iter.remove();
					removed=true;
					break;
				}
			}
			if (removed) {
				continue;
			}
			context=text.getSurroundingText(e.getDrugIndex(), e.getName().length()+10);
			for (String phrase : proximityTriggers) {
				if (StringUtils.containsIgnoreCase(context, phrase)) {
					iter.remove();
					removed=true;
					break;
				}
			}
			
		}
		
	}
	
	
}
