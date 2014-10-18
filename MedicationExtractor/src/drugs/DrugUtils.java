package drugs;

public class DrugUtils {
	
	//checks to see if the two drug entries are referring to the 
	//exact same drug.
	
	//TODO: We may want a more complex algorithm to handle cases
	//where drugs overlap (like "insulin" and "insulin nph")
	public static boolean areSame(DrugEntry a, DrugEntry b) {
		String offset1=a.getDrugNameOffset();
		String offset2=b.getDrugNameOffset();
		
		//if they have the same offset, they are the same thing
		if (a.equals(b)) {
			return true;
		}
		return false;
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
		newEntry.setD(a.getD());
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
