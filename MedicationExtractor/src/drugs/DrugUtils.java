package drugs;

import java.util.List;

import text.DischargeDocument;

public class DrugUtils {
	
	
	/**
	 * Given a DischargeDocument with a set of drugs, filters and merges all duplicate drugs.
	 * At the end of this method, no duplicate drugs will be present in d
	 * @param d
	 */
	public static void filterDuplicateDrugs(DischargeDocument d) {
		while (true) {
			boolean foundDupe=false;
			List<DrugEntry> drugs=d.getDrugEntries();
			if (drugs==null || drugs.size()<2) {
				break; //can't be duplicates without at least 2 drugs
			}
			//pairwise compare all drugs. O(n squared), but with only double-digit drugs that is fine
			
			for (int a=0;a<drugs.size()-1;a++) {
				for (int b=a+1;b<drugs.size();b++) {
					DrugEntry d1=drugs.get(a);
					DrugEntry d2=drugs.get(b);
					if (areSame(d1,d2)) {
						foundDupe=true;
						DrugEntry mergedDrug=mergeEntries(d1,d2);
						
						//remove the two child entries and add back the merged entry, thereby removing the duplicate
						drugs.remove(b);
						drugs.remove(a);
						
						drugs.add(mergedDrug);
						break;
					}
				}
				if (foundDupe) {
					break; //need to restart iteration since we changed the list
				}
			}
			
			
			//no more duplicates, so we are done
			if (!foundDupe) {
				break;
			}
		}
	}
	
	private static boolean doOverlap(int start1, int end1, int start2, int end2) {
		//check for any overlap between the ranges
				if (start1>=start2 && start1<=end2) {
					return true;
				}
				if (end1>=start2 && end1<=end2) {
					return true;
				}
				if (start2>=start1 && start2<=end1) {
					return true;
				}
				if (end2>=start1 && end2<=end1) {
					return true;
				}
				return false;
	}
	
	/**
	 * Checks to see if two drug entries are about the same drug. Two
	 * drugs are marked as being the same if they have any overlap at all between
	 * them in the text. So, "insulin" and "insulin nph" may still be marked as a match.
	 * 
	 * Drugs must match IN THE TEXT. "insulin" and "insulin" do not necessarily match.
	 * @param a
	 * @param b
	 * @return
	 */
	
	
	public static boolean areSame(DrugEntry a, DrugEntry b) {
		int start1=a.getDrugIndex();
		int end1=start1+a.getName().length(); // end is inclusive
		int start2=b.getDrugIndex();
		int end2=start2+b.getName().length();
		boolean drugMatch=doOverlap(start1,end1,start2,end2);
		
		//drugs are not at the same point in the text, so they can't match
		if (!drugMatch) {
			return false;
			
		}
		
		//if they are at the same point in the text, they may still be different if they have different dosages
		if (!a.getDosage().equals("nm") && !b.getDosage().equals("nm")) {
			start1=a.getDosageIndex();
			end1=start1+a.getDosage().length(); // end is inclusive
			start2=b.getDosageIndex();
			end2=start2+b.getDosage().length();
			
			//dosages overlap, so they are the same
			if (doOverlap(start1,end1,start2,end2)) {
				return true;
			}
			//no dosage overlap, so they are different
			return false;
		}
		
		
		//dosages not set, so they are the same
		return true;
	}
	
	/**
	 * Creates a deep copy of the given drug entry and returns it as a new object
	 * @param a
	 * @return
	 */
	public static DrugEntry copyDrug(DrugEntry a) {
		DrugEntry newEntry=new DrugEntry();
		newEntry.setD(a.getDocument());
		//make the start and end as wide as possible based on a and b
		newEntry.setStartIndex(a.getStartIndex());
		newEntry.setEndIndex(a.getEndIndex());
		
		newEntry.setName(a.getName());
		newEntry.setFreq(a.getFreq());
		newEntry.setDosage(a.getDosage());
		newEntry.setMode(a.getMode());
		newEntry.setReason(a.getReason());
		newEntry.setDuration(a.getDuration());
		newEntry.setContext(a.getContext());
		newEntry.setDosageStartIndex(a.getDosageStartIndex());
		
		return newEntry;
	}
	
	/**
	 * Given two drug entries that are the same but which may not have all the same
	 * fields set (like, one entry has freq and the other has a reason), merges
	 * them into a single object with as many fields as possible set.
	 * In cases where a and b have conflicting entries set, a will take precedence,
	 * so make a the best guess.
	 * Returns null if the drug entries are not the same
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	
	
	public static DrugEntry mergeEntries(DrugEntry a, DrugEntry b) {
		if (!areSame(a,b)) {
			return null;
		}
		DrugEntry newEntry=new DrugEntry();
		newEntry.setD(a.getDocument());
		//make the start and end as wide as possible based on a and b
		newEntry.setStartIndex(Math.min(a.getStartIndex(), b.getStartIndex()));
		newEntry.setEndIndex(Math.max(a.getEndIndex(), b.getEndIndex()));
		
		newEntry.setName(a.getName());
		newEntry.setFreq(getAThenB(a.getFreq(),b.getFreq()));
		newEntry.setDosage(getAThenB(a.getDosage(),b.getDosage()));
		newEntry.setMode(getAThenB(a.getMode(),b.getMode()));
		newEntry.setReason(getAThenB(a.getReason(),b.getReason()));
		newEntry.setDuration(getAThenB(a.getDuration(),b.getDuration()));
		newEntry.setContext(getAThenB(a.getContext(),b.getContext()));
		if (a.getDosageStartIndex()!=null) {
			newEntry.setDosageStartIndex(a.getDosageStartIndex());
		} else {
			newEntry.setDosageStartIndex(b.getDosageStartIndex());

		}
		return newEntry;
	}
	
	/**
	 * Returns a if it is not "nm". Otherwise returns b
	 * @param a
	 * @param b
	 * @return
	 */
	private static String getAThenB(String a, String b) {
		if (!a.equals("nm")) {
			return a;
			
		}
		return b;
	}
}
