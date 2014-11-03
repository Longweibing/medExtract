package drugs;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import text.DischargeDocument;

public class FrequencyFinder {
	private static String[] extensions={"prn","p.r.n."};
    private static String[] periodAdj= {"hourly", "daily", "weekly"};

	public static String getNextToken(String s, int index) {
		if (index>s.length()-2) {
			return "";
		}
		s=s.substring(index+1);
		s=s.trim();
		
		return s.substring(0,StringUtils.indexOfAny(s, " \n"));
	}
	
	
	
	
	public static String matchString(String regex,String s) {
		
			//System.out.println(reg);

			Pattern namePattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
			Matcher match=namePattern.matcher(s);
			if (match.find()) {
				return s.substring(match.start(),match.end());
			} 
		
		
		return null;
	}
	
	private static void findFreq(DrugEntry e) {
		int width =60;
		
		if (e.getFreq().equals("nm")) {
			String surroundingStr=e.getDocument().getNextText(e.getDrugIndex(), width);
			//System.out.println("text = "+surroundingStr);
			String match=matchString("\\s+"+DurationFinder.getOrClause(extensions)+"\\s+",surroundingStr);
			if (match!=null) {
				match=match.trim();
				System.out.println("found new frequency = "+match+" | "+e.getName()+" "+e.getDrugNameOffset());
				e.setFreq(match);
				int startIndex=e.getDocument().getText().indexOf(surroundingStr)+surroundingStr.indexOf(match);
				int endIndex=startIndex+match.length();
				e.setStartIndex(Math.min(e.getStartIndex(),startIndex));
				e.setEndIndex(Math.max(e.getEndIndex(),endIndex));

			}
		}
	}
	
	
	private static void improveFreq(DrugEntry e) {
		if (!e.getFreq().equals("nm")) {
			String surroundingText=e.getDocument().getText().substring(e.getIndexOfString(e.getFreq()), e.getIndexOfString(e.getFreq())+e.getFreq().length()+8);
			String match=matchString(e.getFreq()+"\\s*"+DurationFinder.getOrClause(extensions),surroundingText);

			if (match!=null) {
				match=match.trim();
				System.out.println("found better frequency = "+match+" | "+e.getFreq());
				e.setFreq(match);
				int startIndex=e.getDocument().getText().indexOf(surroundingText)+surroundingText.indexOf(match);
				int endIndex=startIndex+match.length();
				e.setStartIndex(Math.min(e.getStartIndex(),startIndex));
				e.setEndIndex(Math.max(e.getEndIndex(),endIndex));
				
			}
		}
	}
	
	public static void findFreqs(DischargeDocument d) {
		for (DrugEntry e : d.getDrugEntries()) {
			improveFreq(e);
		}
		for (DrugEntry e : d.getDrugEntries()) {
			//findFreq(e);
		}
	}
}
