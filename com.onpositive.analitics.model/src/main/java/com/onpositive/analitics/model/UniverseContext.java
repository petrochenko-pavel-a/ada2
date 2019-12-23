package com.onpositive.analitics.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onpositive.clauses.IContext;
import com.onpositive.clauses.ISelector;
import com.onpositive.clauses.ITypedStore;
import com.onpositive.nlp.lexer.EntityRecognizer;
import com.onpositive.nlp.lexer.Lexer;

public class UniverseContext implements IContext,ITypedStore{

	protected HashMap<IType, LinkedHashSet<Object>>instances=new HashMap<>();
	protected LinkedHashSet<Object>allObjects=new LinkedHashSet<>();
	protected EntityRecognizer recognizer=new EntityRecognizer();
	protected Lexer lexer;
	
	@Override
	public ITypedStore store() {
		return this;
	}
	
	public void add(Object o) {
		if (o==null) {
			return;
		}
		if (allObjects.add(o)) {
			IType tpa=TypeProvider.getType(o);
			LinkedHashSet<Object> linkedHashSet = instances.get(tpa);
			if (linkedHashSet==null) {
				linkedHashSet=new LinkedHashSet<>();
				instances.put(tpa, linkedHashSet);
			}
			linkedHashSet.add(o);
			if (tpa instanceof IClass) {
				IClass cm=(IClass) tpa;
				List<IProperty> allProperties = cm.allProperties();
				for (IProperty p:allProperties) {
					if (p.range() instanceof IClass) {
						Object value = p.getValue(o);
						if (value instanceof Collection) {
							Collection<?>cma=(Collection<?>) value;
							cma.forEach(v->add(v));
						}
						else {
							if (value!=null) {
								add(value);
							}
						}
					}
				}
			}
		}
	}

	public void init() {
		this.types().forEach(v->{
			register(v);
			lexer=new Lexer(recognizer);
		});
	}
	
	private void register(IType v) {
		Labels annotation = v.annotation(Labels.class);
		if (annotation!=null) {
			for (String s:annotation.value()) {
				recognizer.addEntity(s, v);
			}
			if (v instanceof IClass) {
				IClass c=(IClass) v;
				c.properties().forEach(p->{
					Labels annotation2 = p.annotation(Labels.class);
					for (String s:annotation2.value()) {
						recognizer.addEntity(s, p);
					}		
				});
			}
		}
	}

	public Collection<IType>types(){
		return instances.keySet();
	}

	@Override
	public Stream<Object> allInstancesOf(IType tp) {
		LinkedHashSet<Object> linkedHashSet = instances.get(tp);
		if (linkedHashSet==null) {
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
