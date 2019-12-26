package com.onpositive.nlp.lexer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.parsers.dates.IFreeFormDate;
import com.onpositive.parsers.dates.MultiRegexDateParser;
import com.onpositive.parsers.dates.MultiRegexDateParser.DateParserType;
import com.onpositive.parsers.dates.templates.DateTemplate.DatesMap;
import com.onpositive.parsers.dates.templates.DateTemplate.DatesMap.DateAndPosition;

public class Lexer {

	private static final int MAX_LEN = 10;
	protected IEntityRecognizer recognizer;

	private static MultiRegexDateParser inst=MultiRegexDateParser.getInstance(DateParserType.CONTEXT_PARSER);
	
	public Lexer(IEntityRecognizer recognizer) {
		super();
		this.recognizer = recognizer;
	}

	int go_left(String str,int positions) {
		for (int i=positions;i>=0;i--) {
			char c=str.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				continue;
			}
			else {
				return i;
			}
		}
		return 0;
	}
	int go_right(String str,int positions) {
		for (int i=positions;i<str.length();i++) {
			char c=str.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				continue;
			}
			else {
				return i;
			}
		}
		return str.length();
	}
	
	public List<List<Object>> lex(String str) {
		DatesMap parseToMap = inst.parseToMap(str,LocalDate.now());
		ArrayList<DateAndPosition> datesArray = parseToMap.getDatesWithPositions();
		int a=0;
		int startOffset=0;
		ArrayList<Object>result=new ArrayList<>();
		for (DateAndPosition f:datesArray) {
			int go_left = go_left(str,f.start);
			String substring = str.substring(startOffset, go_left);
			substring=substring.trim();
			if (!str.isEmpty()) {
				result.addAll(PrimitiveTokenizer.tokenize(substring));
			}
			result.add(f.date);
			startOffset=go_right(str,f.start+f.length);
			a++;
		}
		if (startOffset<str.length()) {
			String substring = str.substring(startOffset, str.length());
			substring=substring.trim();
			if (!str.isEmpty()) {
				result.addAll(PrimitiveTokenizer.tokenize(substring));
			}
		}
		return new ArrayList<>(doRecognition(result));
	}

	@SuppressWarnings("unchecked")
	private Collection<List<Object>> doRecognition(List<? extends Object> tokenize) {
		
		LinkedHashSet<List<Object>> results = new LinkedHashSet<>();
		if (tokenize.isEmpty()) {
			results.add((List<Object>) tokenize);
			return results;
		}
		l2: for (int i = 0; i < tokenize.size(); i++) {
			for (int j = MAX_LEN; j >= 0; j--) {
				List<? extends Object> tokens = subTokens(tokenize, i, j);
				if (tokens == null) {
					continue;
				}
				List<Object> obj = recognizer.tryMatch(tokens);
				if (obj != null && !obj.isEmpty()) {
					List<? extends Object> subList = tokenize.subList(Math.min(i + j, tokenize.size()),
							tokenize.size());
					// System.out.println(subList);
					Collection<List<Object>> doRecognition = doRecognition(subList);
					if (obj.size() > 1 && obj.stream().allMatch(x -> x instanceof IHasKind)) {
						if (obj.stream().map(v -> ((IHasKind) v).kind()).distinct().count() == 1) {
							StringBuilder bld = new StringBuilder();
							for (Object o : tokens) {
								bld.append(o.toString());
							}
							obj = Collections.singletonList(new OrToken(bld.toString(),obj));
						}
					}
					for (Object o : obj) {
						for (List<Object> rec : doRecognition) {
							ArrayList<Object> result = new ArrayList<>();
							result.addAll(tokenize.subList(0, i));
							result.add(o);
							result.addAll(rec);
							results.add(result);
						}
					}
					// if (j>1){
					break l2;
					// }//break l2;
				}
			}
		}
		if (results.isEmpty()) {
			results.add((List<Object>) tokenize);
		}
		return results;
	}

	static HashSet<String> separators = new HashSet<>();

	static {
		separators.add("_");
		separators.add("-");
	}

	
	
	private static List<? extends Object> subTokens(List<? extends Object> tokenize, int i, int j) {
		if (j + i > tokenize.size()) {
			j = tokenize.size() - i;
		}
		List<? extends Object> subList = tokenize.subList(i, j + i);
		if (subList.size() > 3) {
			ArrayList<Object> mm = new ArrayList<>();
			for (int a = 0; a < subList.size(); a++) {
				Object item = subList.get(a);
				if (separators.contains(item)) {
					continue;
				} else {
					mm.add(item);
				}
			}
			if (mm.size() < 5&&mm.size()!=subList.size()) {
				return mm;
			}
			return null;
		}
		return subList;
	}

}
