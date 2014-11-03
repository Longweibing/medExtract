package drugs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import text.DischargeDocument;
import main.FileUtils;

public class ReasonFinder {
	private static List<String> reasons;
	private static List<String> bodyParts;

	private static String[] directions={"left","right","anterior","posterior","distal","proximal",
		"dorsal","ventral","superior","inferior","lateral","medial","rostral","caudal"};
	private static String[] modifiers={"severe","acute","chronic","dull","sharp","stabbing"};
	public static void loadReasons(File f) {
		reasons=new ArrayList<String>();
		String r=FileUtils.readFile(f);
		for (String s : r.split("\n")) {
			if (s.trim().isEmpty()) {
				continue;
			}
			reasons.add(s.trim().toLowerCase());
		}
	}
	
	public static void loadBodyParts(File f) {
		bodyParts=new ArrayList<String>();
		String r=FileUtils.readFile(f);
		for (String s : r.split("\n")) {
			if (s.trim().isEmpty()) {
				continue;
			}
			bodyParts.add(s.trim().toLowerCase());
		}
	}
	
	public static List<String> getLongRegexes() {
		List<String> regexes=new ArrayList<String>();
		StringBuilder sb=new StringBuilder();
		//sb.append("(for)?");
		sb.append("\\s*");
		sb.append(getOrClause(directions));
		sb.append("?");
		sb.append("\\s*");
		sb.append(getOrClause(bodyParts));
		sb.append("?");
		sb.append("\\s*");
		sb.append(getOrClause(reasons));
		sb.append("\\.?");
		regexes.add(sb.toString());
		return regexes;
	}
	
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
	
	public static String getOrClause(List<String> arr) {
		StringBuilder sb=new StringBuilder();
		sb.append("(");
		sb.append(arr.get(0));
		for (int i=1;i<arr.size();i++) {
			sb.append("|");
			sb.append(arr.get(i));
		}
		sb.append(")");
		return sb.toString();
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
	public static String matchString(List<String> regex,String s) {
		for (String reg : regex) {
			//System.out.println(reg);

			Pattern namePattern=Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
			Matcher match=namePattern.matcher(s);
			if (match.find()) {
				return s.substring(match.start(),match.end()).trim();
			} 
		}
		
		return null;
	}
	
	
	
	private static void findReason(DrugEntry e, List<Integer> indices) {
		int width=100;
		int prevWidth=width;
		int forWidth=width;
		if (e.getReason().equals("nm")) {
			int curIndex=indices.indexOf(e.getDrugIndex());
			if (curIndex>0 && curIndex<indices.size()) {
				int nextDrug=indices.get(curIndex-1);
				int distance=Math.abs(nextDrug-e.getDrugIndex());
				prevWidth=Math.min(prevWidth, distance);
			}
			if (curIndex>=0 && curIndex<indices.size()-1) {
				int nextDrug=indices.get(curIndex+1);
				int distance=Math.abs(nextDrug-e.getDrugIndex());
				forWidth=Math.min(forWidth, distance);
			}
			String surroundingStr=e.getDocument().getPrevText(e.getDrugIndex(), prevWidth)+e.getDocument().getNextText(e.getDrugIndex(), forWidth);
			//System.out.println("text = "+surroundingStr);
			String match=matchString(getLongRegexes(),surroundingStr);
			if (match!=null) {
				match=match.trim();
				//System.out.println("found new Reason = "+match+" | "+e.getName()+" "+e.getDrugNameOffset());
				e.setReason(match);
				int startIndex=e.getDocument().getText().indexOf(surroundingStr)+surroundingStr.indexOf(match);
				int endIndex=startIndex+match.length();
				e.setStartIndex(Math.min(e.getStartIndex(),startIndex));
				e.setEndIndex(Math.max(e.getEndIndex(),endIndex));

			}
		}

	}
	
	public static void findReasons(DischargeDocument d) {
		List<Integer> indices=new ArrayList<Integer>();
		for (DrugEntry e : d.getDrugEntries()){
			indices.add(e.getDrugIndex());
		}
		Collections.sort(indices);
		for (DrugEntry e : d.getDrugEntries()) {
			findReason(e,indices);
		}
	}
	
	
	
	
	
	
}
