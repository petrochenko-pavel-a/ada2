package com.onpositive.analitics.model.java;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IType;
import com.onpositive.clauses.IUniverse;

public class Universe implements IUniverse{

	protected LinkedHashSet<IType>tpes=new LinkedHashSet<>();
	
	public void add(IType tp) {
		if (tpes.add(tp)) {
			innerAdd(tp);
		}
	}

	private void innerAdd(IType tp) {
		if (tp instanceof IClass) {
			((IClass) tp).allProperties().forEach(v->{
				add(v.range());
			});
		}
	}

	@Override
	public Collection<IClass> classes() {
		return tpes.stream().filter(x->x instanceof IClass).map(x->(IClass)x).collect(Collectors.toList());
	}
	
	
}
