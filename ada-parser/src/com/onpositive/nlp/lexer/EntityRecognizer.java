package com.onpositive.nlp.lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.atteo.evo.inflector.English;

import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.swabunga.spell.engine.SpellDictionaryHashMap;

public class EntityRecognizer implements IEntityRecognizer {

	protected HashMap<String, ArrayList<Object>> entities = new HashMap<>();

	protected SpellDictionaryHashMap sp;

	public EntityRecognizer() {
		try {
			sp = new SpellDictionaryHashMap();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void addEntity(String entityName, Object value) {
		if (entityName.length() == 0) {
			return;
		}

		if (entityName.indexOf(' ') != -1) {
			String[] split = entityName.split(" ");
			if (split.length > 1) {
				for (String sa : split) {
					if (sa.isEmpty()) {
						continue;
					}
					if (sa.length() < 3) {
						continue;
					}
					if (Character.toLowerCase(sa.charAt(0)) != sa.charAt(0)) {
						innerEnd(sa.toLowerCase(), value);
					}
				}
			}
		}

		entityName = entityName.toLowerCase();
		innerEnd(entityName, value);
		entityName = entityName.replace(" ", "");
		entityName = entityName.replace("_", "");
		entityName = entityName.replace("-", "");
		innerEnd(entityName, value);
		if (!(value instanceof IUncountable)) {
		innerEnd(English.plural(entityName), value);
		}

	}

	public void innerEnd(String entityName, Object value) {

		innerInnerEnd(entityName, value);
	}

	protected void innerInnerEnd(String entityName, Object value) {
		ArrayList<Object> arrayList = entities.get(entityName);
		if (arrayList == null) {
			arrayList = new ArrayList<>();
			entities.put(entityName, arrayList);
		}
		arrayList.add(value);
		if (entityName.length() < 25) {
			sp.addWord(entityName);
		}
	}

	public static HashSet<String> pref = new HashSet<>();

	static {
		pref.add("not");
		pref.add("are");
		pref.add("was");
		// pref.add("were");
		// pref.add("that");
	}

	@Override
	public ArrayList<Object> tryMatch(List<? extends Object> tokens) {
		StringBuilder bld = new StringBuilder();
		if (tokens.size() > 1 && pref.contains(tokens.get(0))) {

			return null;
		}

		boolean startsWithNumb = false;
		if (tokens.size() > 0 && (tokens.get(0) instanceof Number)) {
			startsWithNumb = true;
		}

		for (Object o : tokens) {
			bld.append(o.toString());
		}
		if (bld.length() == 0) {
			return null;
		}
		String lowerCase = bld.toString().toLowerCase();

		ArrayList<Object> arrayList = entities.get(lowerCase);
		if (arrayList == null) {
			arrayList = new ArrayList<>();
		}
		GrammarRelation[] possibleGrammarForms = WordNetProvider.getInstance().getPossibleGrammarForms(lowerCase);
		if (possibleGrammarForms != null) {
			for (GrammarRelation c : possibleGrammarForms) {
				if (entities.containsKey(c.getWord().getBasicForm())) {
					ArrayList<Object> arrayList2 = entities.get(c.getWord().getBasicForm());
					arrayList.addAll(arrayList2);
				}
				if (SynonimForms.synonims.containsKey(c.getWord().getBasicForm())) {
					String string = SynonimForms.synonims.get(c.getWord().getBasicForm());
					ArrayList<Object> arrayList2 = entities.get(string);
					arrayList.addAll(arrayList2);
				}
			}
		}
		if (arrayList.isEmpty()) {
			if (SynonimForms.synonims.containsKey(lowerCase)) {
				String string = SynonimForms.synonims.get(lowerCase);
				arrayList = entities.get(string);
				if (string == null) {
					possibleGrammarForms = WordNetProvider.getInstance().getPossibleGrammarForms(string);
					if (possibleGrammarForms != null) {
						for (GrammarRelation c : possibleGrammarForms) {
							if (entities.containsKey(c.getWord().getBasicForm())) {
								ArrayList<Object> arrayList2 = entities.get(c.getWord().getBasicForm());
								arrayList.addAll(arrayList2);
							}
						}
					}
				}
			}
		}
		if (arrayList.isEmpty()) {
			return null;
		}
		// if (!startsWithNumb&&arrayList == null && ((lowerCase.length() >
		// 3&&tokens.size()==1)||(lowerCase.length() > 6&&tokens.size()>1))&&
		// !AllMatchParser.isDEBUG()) {
		// List<Word> correct = sp.getSuggestions(lowerCase, 100);
		// if (correct != null && !correct.isEmpty()) {
		// String corr = correct.get(0).getWord().toLowerCase();
		// arrayList = entities.get(corr);
		// if (tokens.size() > 1) {
		// String string = arrayList.iterator().next().toString();
		// if (string.indexOf(' ') == -1) {
		// return null;
		// }
		// }
		// }
		// }
		return arrayList;
	}
}
