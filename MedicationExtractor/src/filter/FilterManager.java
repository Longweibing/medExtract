package filter;

import java.util.ArrayList;
import java.util.List;

import drugs.DrugEntry;
import text.DischargeDocument;

public class FilterManager {
	
	private static List<Filter> filters=new ArrayList<Filter>();

	public static void loadFilters() {
		
		filters.add(new AbbreviationFilter());
		filters.add(new AllergyFilter());
		filters.add(new SectionFilter());
		filters.add(new InsulinFilter());
	}
	
	private static int isNM(String s) {
		if (!s.equals("nm")) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * Counts the number of fields (excluding name and context) that are set
	 * in this drug. This is a useful measurement of whether we have found a drug,
	 * as false positives are more likely to have nothing set
	 * @param e
	 * @return
	 */
	
	public static int countFields(DrugEntry e) {
		int count=0;
		count+=isNM(e.getDosage());
		count+=isNM(e.getFreq());
		count+=isNM(e.getDuration());
		count+=isNM(e.getReason());
		count+=isNM(e.getMode());
		
		return count;
	}
	
	
	
	public static void runAllFilters(DischargeDocument text) {
		
		
		for (Filter f : filters) {
			f.FilterDrugs(text);
		}
	}
}
