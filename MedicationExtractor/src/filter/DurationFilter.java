package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import drugs.DrugEntry;
import drugs.DrugUtils;
import text.DischargeDocument;

/**
 * This class will filter out bad durations. It will not filter out any drugs themselves
 * @author Eric
 *
 */

public class DurationFilter extends Filter {
	private static String[] badDurations={"xtend","tend"};
	//if two different drugs have the same duration, removes the duration from teh drug that is farther away
	private void filterMatchingDurations(List<DrugEntry> drugs) {
		for (int i=0;i<drugs.size()-1;i++) {
			for (int i2=i+1;i2<drugs.size();i2++) {
				
				DrugEntry d1=drugs.get(i);
				DrugEntry d2=drugs.get(i2);
				if (d1.getDuration().equals("nm") || d2.getDuration().equals("nm")) {
					continue;
				}
				int s1=d1.getDurationIndex();
				int e1=s1+d1.getDuration().length();
				int s2=d2.getDurationIndex();
				int e2=s2+d2.getDuration().length();
				
				if (!DrugUtils.areSame(d1, d2, false) &&DrugUtils.doOverlap(s1, e1, s2, e2)) {
					int distance1=Math.min(Math.abs(s1-d1.getDrugIndex()), Math.abs(e1-d1.getDrugIndex()));
					int distance2=Math.min(Math.abs(s2-d2.getDrugIndex()),Math.abs(e2-d2.getDrugIndex()));
					if (distance1<=distance2) {
						//System.out.println("filtering bad duration dupe!");
						//System.out.println(d2.getName()+" "+d2.getDrugNameOffset()+" "+d2.getDuration());

						d2.setDuration(null);
					} else {
						//System.out.println("filtering bad duration dupe!");

						//System.out.println(d1.getName()+" "+d1.getDrugNameOffset()+" "+d1.getDuration());
						d1.setDuration(null);


					}
				}
			}
		}
	}
	
	private void filterBadDurations(List<DrugEntry> drugs) {
		for (DrugEntry e : drugs) {
			String normalDuration=e.getDuration().toLowerCase().trim();
			if (normalDuration.equals("nm")) {
				continue;
			}
			for (String bad : badDurations) {
				bad=bad.trim().toLowerCase();
				if (bad.equals(normalDuration)) {
					e.setDuration(null);
				} else if (normalDuration.length()<=4 && normalDuration.endsWith("h")) {
					e.setDuration(null); //exclude stuff like 1h
				} else if (normalDuration.contains("year") || normalDuration.contains("minute")) {
					e.setDuration(null); //that really is not likely
				} else if (e.getFreq().contains(e.getDuration())) {
					
					e.setDuration(null);
				}
				break;
			}
		}
	}
	
	//every duration should be found immediately after its drug. In other words,
	//going backwards from the duration index to the drug index should not pass
	//any other drugs
	
	@Override
	public void FilterDrugs(DischargeDocument text) {
		filterBadDurations(text.getDrugEntries());
		filterMatchingDurations(text.getDrugEntries());
		List<Integer> drugLocations=new ArrayList<Integer>();
		for (DrugEntry e : text.getDrugEntries()) {
			drugLocations.add(e.getDrugIndex());
		}
		Collections.sort(drugLocations);
		for (DrugEntry e : text.getDrugEntries()) {
			if (!e.getDuration().equals("nm")) {
				int durationIndex=e.getIndexOfString(e.getDuration());
				int drugIndex=-1;
				if (e.getDrugIndex()< e.getDurationIndex()) {
					
					for (int i=0;i<drugLocations.size();i++) {
						if (drugLocations.get(i)>durationIndex){ //this is the first drug PAST the duration
							drugIndex=i-1; //drug that should correspond to the reason
							break;
						}
					}
					if (drugIndex>=0) {
						if (drugLocations.get(drugIndex)!=e.getDrugIndex()) {
							
							//System.out.println("filtering out a bad duration!");
							//System.out.println(e.getName()+" "+e.getDrugNameOffset()+" "+e.getDuration());
							e.setDuration(null);
						}
					}
				} else {
					//otherwise,  we need to make sure the drug is the next one forward
					for (int i=drugLocations.size()-1;i>=0;i--) {
						if (drugLocations.get(i)<durationIndex){ //this is the first drug PAST the duration
							drugIndex=i+1; //drug that should correspond to the duration
							break;
						}
					}
					if (drugIndex<=drugLocations.size()-1 && drugIndex>=0) {
						if (drugLocations.get(drugIndex)!=e.getDrugIndex()) {
							
							//System.out.println("filtering out a bad duration!");
							//System.out.println(drugLocations.get(drugIndex)+" "+e.getDurationOffset());
							//System.out.println(e.getName() + " "+e.getDuration());
							e.setDuration(null);
						}
					}
				}
				
			}
		}
		
	}

}
