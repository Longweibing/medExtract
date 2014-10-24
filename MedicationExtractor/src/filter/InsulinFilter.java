package filter;


/**
 * This filter removes mentions of insulin due to "insulin dependent diabetes"
 */
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import drugs.DrugEntry;
import text.DischargeDocument;

public class InsulinFilter extends Filter {

	@Override
	public void FilterDrugs(DischargeDocument text) {
		Iterator<DrugEntry> iter = text.getDrugEntries().iterator();
		while (iter.hasNext()) {
			DrugEntry e=iter.next();
			if (StringUtils.containsIgnoreCase(e.getName(), "insulin")) {
				String context=text.getSurroundingText(e.getDrugIndex(), 60);
				System.out.println(context);
				if (StringUtils.containsIgnoreCase(context, "dependent")) {
					iter.remove();
				}
			}
		}		
	}

}
