package drugs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import text.DischargeDocument;

public class DurationFinder {
    private static String[] months={"january","february","march","april","may","june","july","august","september","october","november",
    	"december","jan","feb","mar","apr","may","jun","jul","aug","sept","oct","nov","dec"};
    private static String[] daysOfWeek={"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday","mon","tues","wed","thurs","fri","sat","sun"};
	private static String[] courseTerms={"packs","courses","total course","course","pack"};
	private static String[] writtenNumber={"\\d+","one","two","three","four","five","six","seven","eight","nine","ten",
		"eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","ninteen","ninteen",
		"twenty","thirty","forty","fifty","sixty","seventy","eighty","ninety"};
	private static String[] period={"hours","wks", "hrs", "days", "weeks", "months","packs","pack", "doses","dose","hour", "hr", "h", "day", "d", "week", "wk", "w", "month", "mo"};
	private static String[] forTerms={"x","for", "for an additional", "for a", "for an","times"};

	
	
	private static String[] longPeriod={"days", "weeks", "months","doses","dose", "day", "d", "week","wks", "wk", "w", "month", "mo"};

	private static String[] timesOfDay={"morning","afternoon","evening","night","noon","midnight","sunset","sunrise",
		"mid-morning","lunchtime","lunch","breakfast","dinner","breakfasttime","dinnertime","supper","sundown","mealtime",
		"dusk","daybreak"};
	private static String[] timePrepositions={"until"};
	
	private static String[] during={"during"};
	
	private static String[] modifiers={"more","extra","additional","total"};
	
	private static String[] linkers={"the following","the next","tomorrow","this","next"};
	private static String[] joiners={"to","-","/","through","and","until"};
	
	private static String[] from={"from"};
	
	private static String dateRegex="\\d{1,2}[-/\\\\]\\d{1,2}([-/\\\\]\\d{2,4})?";
	public static String getOrClause(String[] arr) {
		StringBuilder sb=new StringBuilder();
		sb.append("(");
		sb.append(arr[0]);
		for (int i=1;i<arr.length;i++) {
			sb.append("|");
			sb.append(arr[i]);
		}
		sb.append(")");
		return sb.toString();
	}
	
	private static String[] mergeArrs(String[] arr1, String[] arr2) {
		String[] newArr=new String[arr1.length+arr2.length];
		int index=0;
		for (int i=0;i<arr1.length;i++) {
			newArr[index]=arr1[i];
			index++;
		}
		for (int i=0;i<arr2.length;i++) {
			newArr[index]=arr2[i];
			index++;
		}
		return newArr;
	}
	
	public static List<String> getLongRegexes() {
		List<String> regexes=new ArrayList<String>();

		
		
		
		regexes.add("(\\d+)?\\s*(total)?\\s*(doses)?\\s*(number|#)\\s*of\\s*doses\\s*(required)?\\s*(\\(?\\s*(approximate)?\\s*\\)?\\s*)?\\s*(:)?\\s*(\\d+)?");
		return regexes;
	}
	
	public static List<String> getShortRegexes() {
		List<String> regexes=new ArrayList<String>();
		//first regex is for things like "for five weeks" and such"
		StringBuilder sb=new StringBuilder();
		
		sb.append(getOrClause(forTerms));
		sb.append("\\s*");
		sb.append(getOrClause(linkers));
		sb.append("?");
		sb.append("\\s*");
		sb.append(getOrClause(writtenNumber));
		sb.append("(\\s*[-/\\\\]\\s*"+getOrClause(writtenNumber)+"\\s*)?");
		sb.append("\\s*");
		sb.append(getOrClause(modifiers));
		sb.append("?");
		sb.append("\\s");
		sb.append(getOrClause(period));
		sb.append("\\s*");
		sb.append(getOrClause(courseTerms));
		sb.append("?");
		sb.append("\\s*");
		sb.append("(as needed)");
		sb.append("?");
		regexes.add(sb.toString());
		
		
		//second regex is for things that include "until"
		sb=new StringBuilder();
		
		sb.append(getOrClause(timePrepositions));
		sb.append("\\s*");
		sb.append(getOrClause(linkers));
		sb.append("\\s*");
		sb.append(getOrClause(mergeArrs(mergeArrs(timesOfDay,daysOfWeek),mergeArrs(period,months))));
		sb.append("\\s*");
		sb.append(getOrClause(courseTerms));
		sb.append("?");
		sb.append("\\s*");
		sb.append("(as needed)");
		sb.append("?");
		regexes.add(sb.toString());
		
		
		sb=new StringBuilder();
		
		sb.append(getOrClause(timePrepositions));
		sb.append("\\s*");
		sb.append(getOrClause(linkers));
		sb.append("?");
		sb.append("\\s*");
		sb.append(getOrClause(mergeArrs(mergeArrs(timesOfDay,daysOfWeek),months)));
		sb.append("\\s*");
		sb.append(getOrClause(courseTerms));
		sb.append("?");
		sb.append("\\s*");
		sb.append("(as needed)");
		sb.append("?");
		regexes.add(sb.toString());
		
		
		
		sb=new StringBuilder();
		
		//trying to match date ranges
		sb=new StringBuilder();
		//sb.append(getOrClause(from));
		//sb.append("?");
		sb.append("\\s*");
		sb.append(dateRegex);
		sb.append("\\s*");
		sb.append(getOrClause(joiners));
		sb.append("\\s*");
		sb.append(dateRegex);
		sb.append("\\s*");
		sb.append("(as needed)");
		sb.append("?");
		regexes.add(sb.toString());

		regexes.add("\\d+\\s*"+getOrClause(modifiers)+"?\\s*doses");
		regexes.add("as long as needed");
		regexes.add("(for a |for an)\\s*"+getOrClause(period));
		regexes.add("during spring break");
		regexes.add(getOrClause(writtenNumber)+"\\s*[-\\s{1}\\\\]\\s*"+getOrClause(period)+"\\s*"+getOrClause(courseTerms));
		return regexes;
	}
	
	public static String matchString(List<String> regex,String s) {
		for (String reg : regex) {
			//System.out.println(reg);

			Pattern namePattern=Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
			Matcher match=namePattern.matcher(s);
			if (match.find()) {
				return s.substring(match.start(),match.end());
			} 
		}
		
		return null;
	}
	
	public static List<String> allMatchString(List<String> regex,String s) {
		List<String> matches=new ArrayList<String>();
		for (String reg : regex) {
			//System.out.println(reg);

			Pattern namePattern=Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
			Matcher match=namePattern.matcher(s);
			while (match.find()) {
				
				matches.add(s.substring(match.start(),match.end()));
			} 
		}
		
		return matches;
	}
	
	private static void improveDuration(DrugEntry e) {
		int width=120;
		if (!e.getDuration().equals("nm")) {
			String duration=e.getDuration();
			String surroundingStr=e.getDocument().getSurroundingText(e.getDurationIndex(), width);
			
			List<String> matches=allMatchString(getLongRegexes(),surroundingStr);
			for (String match : matches) {
				if (match.contains(duration) && !match.equals(duration)) {
					match=match.trim();
					System.out.println("found better duration = "+match+" | "+e.getDuration());
					e.setDuration(match);
					int startIndex=e.getDocument().getText().indexOf(surroundingStr)+surroundingStr.indexOf(match);
					int endIndex=startIndex+match.length();
					e.setStartIndex(Math.min(e.getStartIndex(),startIndex));
					e.setEndIndex(Math.max(e.getEndIndex(),endIndex));
					
				}
			}
			
		}
		
		width=80;
		if (!e.getDuration().equals("nm")) {
			String duration=e.getDuration();
			String surroundingStr=e.getDocument().getSurroundingText(e.getDurationIndex(), width);
			
			List<String> matches=allMatchString(getShortRegexes(),surroundingStr);
			for (String match : matches) {
				if (match.contains(duration) && !match.equals(duration)) {
					match=match.trim();
					System.out.println("found better duration = "+match+" | "+e.getDuration());
					e.setDuration(match);
					int startIndex=e.getDocument().getText().indexOf(surroundingStr)+surroundingStr.indexOf(match);
					int endIndex=startIndex+match.length();
					e.setStartIndex(Math.min(e.getStartIndex(),startIndex));
					e.setEndIndex(Math.max(e.getEndIndex(),endIndex));
					
				}
			}
		}
	}
	
	private static void findDuration(DrugEntry e) {
		int width=200;
		if (e.getDuration().equals("nm")) {
			String surroundingStr=e.getDocument().getNextText(e.getDrugIndex(), width);
			//System.out.println("text = "+surroundingStr);
			String match=matchString(getLongRegexes(),surroundingStr);
			if (match!=null) {
				match=match.trim();
				//System.out.println("found new duration = "+match+" | "+e.getName()+" "+e.getDrugNameOffset());
				e.setDuration(match);
				int startIndex=e.getDocument().getText().indexOf(surroundingStr)+surroundingStr.indexOf(match);
				int endIndex=startIndex+match.length();
				e.setStartIndex(Math.min(e.getStartIndex(),startIndex));
				e.setEndIndex(Math.max(e.getEndIndex(),endIndex));

			}
		}
		
		width =130;
		
		if (e.getDuration().equals("nm")) {
			String surroundingStr=e.getDocument().getNextText(e.getDrugIndex(), width);
			//System.out.println("text = "+surroundingStr);
			String match=matchString(getShortRegexes(),surroundingStr);
			if (match!=null) {
				match=match.trim();
				//System.out.println("found new duration = "+match+" | "+e.getName()+" "+e.getDrugNameOffset());
				e.setDuration(match);
				int startIndex=e.getDocument().getText().indexOf(surroundingStr)+surroundingStr.indexOf(match);
				int endIndex=startIndex+match.length();
				e.setStartIndex(Math.min(e.getStartIndex(),startIndex));
				e.setEndIndex(Math.max(e.getEndIndex(),endIndex));

			}
		}
	}
	
	public static void findDurations(DischargeDocument d) {
		for (DrugEntry e : d.getDrugEntries()) {
			improveDuration(e);
			findDuration(e);
		}
	}



}
