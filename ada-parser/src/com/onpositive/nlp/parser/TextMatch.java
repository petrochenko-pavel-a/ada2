package com.onpositive.nlp.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import com.onpositive.nlp.lexer.SynonimForms;

public class TextMatch implements SyntacticPredicate{

	protected HashSet<String>matches=new HashSet<>();
	
	public TextMatch(String...options){
		for (String o: options){
			matches.add(o);
		}
	}
	
	@Override
	public int tryParse(List<?> v, int pos, HashMap<String, Object> vars) {
		if (pos>=v.size()){
			return -1;
		}
		Object ma = v.get(pos);
		if (matches.contains(ma)) return 1;
		if (ma instanceof String) {
			for (String s:matches) {
				if (SynonimForms.isPossibleReplacement((String) ma, s)) {
					return 1;
				}
			}
		}
		return -1;
	}

	public HashSet<String> get() {
		return matches;
	}

	@Override
	public void gatherLiterals(Consumer<String> c) {
		matches.forEach(m->c.accept(m));
	}

}
