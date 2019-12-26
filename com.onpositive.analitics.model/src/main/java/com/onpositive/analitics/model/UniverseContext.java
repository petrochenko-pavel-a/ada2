package com.onpositive.analitics.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ada.model.Comparative;
import com.ada.model.GenericTime;
import com.ada.model.Preposition;
import com.ada.model.SimpleDisambiguator;
import com.onpositive.clauses.IContext;
import com.onpositive.clauses.IHasContext;
import com.onpositive.clauses.ISelector;
import com.onpositive.clauses.ITypedStore;
import com.onpositive.clauses.impl.AllInstancesOf;
import com.onpositive.clauses.impl.SingleSelector;
import com.onpositive.clauses.impl.ThemInstances;
import com.onpositive.nlp.lexer.DatePart;
import com.onpositive.nlp.lexer.EntityRecognizer;
import com.onpositive.nlp.lexer.Lexer;
import com.onpositive.nlp.lexer.OrToken;
import com.onpositive.nlp.parser.AllMatchParser;

public class UniverseContext implements IContext, ITypedStore {

	protected HashMap<IType, LinkedHashSet<Object>> instances = new HashMap<>();
	protected LinkedHashSet<Object> allObjects = new LinkedHashSet<>();
	protected EntityRecognizer recognizer = new EntityRecognizer();
	protected Lexer lexer;
	private AllMatchParser<Object> parser;

	@Override
	public ITypedStore store() {
		return this;
	}

	public ParsedQuery execute(String query) {
		List<List<Object>> lex = lexer.lex(query);
		SimpleDisambiguator simpleDisambiguator = new SimpleDisambiguator();
		LinkedHashSet<List<Object>> results = new LinkedHashSet<>();
		for (List<Object> seq : lex) {
			Collection<List<Object>> parse = parser.parse(toSelectors(seq));
			results.addAll(simpleDisambiguator.disambiguate(parse));			
		}
		LinkedHashSet<List<Object>> gresults = new LinkedHashSet<>();
		int ms = Integer.MAX_VALUE;
		for (List<Object> o : results) {
			if (o.size() < ms) {
				ms = o.size();
			}
		}
		for (List<Object> o : results) {
			if (o.size() == ms) {
				gresults.add(o);
			}
		}
//		for (List<Object> o : gresults) {
//			if (o.size() == 1) {
//				Object object = o.get(0);
//				if (object instanceof IParsedEntity) {
//					IParsedEntity p = (IParsedEntity) object;
//					p.initFromContext(ct.clone());
//				}
//			}
//		}
		if (gresults.size() > 0) {
			if (ms == 1) {
				List<List<Object>> collect = gresults.stream()
						.filter(v -> v.stream().filter(va -> va instanceof ISelector).findAny().isPresent())
						.collect(Collectors.toList());
				if (!collect.isEmpty()) {
					return new ParsedQuery(query,this,new ArrayList<>(simpleDisambiguator.finalDisambig(collect)));
				}
			}
			return new ParsedQuery(query,this,new ArrayList<>(simpleDisambiguator.finalDisambig(gresults)));
		}
		return new ParsedQuery(query,this,new ArrayList<>(simpleDisambiguator.finalDisambig(results)));
	}

	private List<Object> toSelectors(List<Object> seq) {
		ArrayList<Object> res = new ArrayList<>();
		for (Object o : seq) {
			if (o instanceof DatePart) {
				res.add(new GenericTime((DatePart) o));
			} else if (o instanceof IClass) {
				if (o == Builtins.ALLMATCH) {
					ThemInstances ti = new ThemInstances();
					res.add(ti);
				} else {
					AllInstancesOf ali = new AllInstancesOf((IType) o);
					res.add(ali);
				}
			}
			// else if (o instanceof Number) {
			// SingleSelector e = new SingleSelector(o, Builtins.NUMBER);
			// res.add(e);
			// }
			else if (o instanceof OrToken) {
				OrToken tk = (OrToken) o;
				Object en = tk.options.iterator().next();
				SingleSelector e = new SingleSelector(tk.options, TypeProvider.getType(en));
				e.setLabel(tk.toString());
				res.add(e);
			} else if (o instanceof IEntity) {
				SingleSelector se = new SingleSelector(o, TypeProvider.getType(o));
				res.add(se);
			} else {
				res.add(o);
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public void add(Object o) {
		if (o == null) {
			return;
		}
		if (allObjects.add(o)) {
			IType tpa = TypeProvider.getType(o);
			LinkedHashSet<Object> linkedHashSet = instances.get(tpa);
			if (linkedHashSet == null) {
				linkedHashSet = new LinkedHashSet<>();
				instances.put(tpa, linkedHashSet);
			}
			linkedHashSet.add(o);
			if (tpa instanceof IClass) {
				IClass cm = (IClass) tpa;

				IProperty xa = cm.keyProperty();
				if (xa != null) {
					Object value = xa.getValue(o);
					if (value != null) {
						if (value instanceof Collection) {
							@SuppressWarnings("rawtypes")
							Collection xc = (Collection) value;
							xc.forEach(v -> {
								recognizer.addEntity(v.toString(), o);
							});
						} else {
							recognizer.addEntity(value.toString(), o);
						}
					}
				}
				List<IProperty> allProperties = cm.allProperties();
				for (IProperty p : allProperties) {
					if (p.range() instanceof IClass) {
						if (!p.isLazy()) {
							Object value = p.getValue(o);
							if (value instanceof Collection) {
								Collection<?> cma = (Collection<?>) value;
								cma.forEach(v -> add(v));
							} else {
								if (value != null) {
									add(value);
								}
							}
						}
					}
				}
			}
		}
	}

	public void init(AllMatchParser<Object> load) {
		this.types().forEach(v -> {
			register(v);
			lexer = new Lexer(recognizer);
		});
		load.addInterceptor(x->{
			if (x instanceof IHasContext) {
				((IHasContext) x).setContext(this);
			}
		});;
		Comparative.init(recognizer);
		Preposition.init(recognizer);
		load.literals().forEach(v -> {
			this.recognizer.addEntity(v, v);
		});
		this.parser = load;
	}

	private void register(IType v) {
		Labels annotation = v.annotation(Labels.class);
		if (annotation != null) {
			for (String s : annotation.value()) {
				recognizer.addEntity(s, v);
			}
			if (v instanceof IClass) {
				IClass c = (IClass) v;
				c.properties().forEach(p -> {
					Labels annotation2 = p.annotation(Labels.class);
					if (annotation2 != null) {
						for (String s : annotation2.value()) {
							recognizer.addEntity(s, p);
						}
					}
				});
			}
		}
	}

	public Collection<IType> types() {
		return instances.keySet();
	}

	@Override
	public Stream<Object> allInstancesOf(IType tp) {
		LinkedHashSet<Object> linkedHashSet = instances.get(tp);
		if (linkedHashSet == null) {
			return Collections.emptyList().stream();
		}
		return linkedHashSet.stream();
	}

	@Override
	public Collection<Object> execute(ISelector sl) {
		return sl.values(this).parallel().collect(Collectors.toList());
	}

	public int size() {
		return allObjects.size();
	}

}
