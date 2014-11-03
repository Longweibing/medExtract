package filter;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import drugs.DrugEntry;
import text.DischargeDocument;

public class NegationFilter extends Filter {

	String[] negations={"nothing","can't","none","never","isn't","shouldn't","without", "not","didn't","won't","hasn't","no"};
	
	@Override
	public void FilterDrugs(DischargeDocument text) {

		Iterator<DrugEntry> iter = text.getDrugEntries().iterator();
		while (iter.hasNext()) {
			DrugEntry e=iter.next();
			boolean removed=false;
			String context=text.getPrevText(e.getDrugIndex(), 20);
			String standardContext=context.toLowerCase().trim();
			String[] tokens=standardContext.split("\\s");
		
			for (String s : tokens) {
				for (String neg : negations) {
					if (s.equals(neg)) {
						iter.remove();
						removed=true;
						break;
					}
				}
				if (removed) {
					break;
				}
				
			}
			
			
		}
		
	}
}
