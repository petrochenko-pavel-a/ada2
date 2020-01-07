package com.onpositive.clauses.impl;

import java.util.Collection;

import com.onpositive.analitics.model.Builtins;
import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.ICompoundProperty;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;

public class NumberProperty implements IProperty,ICompoundProperty{

	public final IProperty ps;
	
	public NumberProperty(IProperty ps) {
		super();
		this.ps = ps;
	}

	@Override
	public String name() {
		return "count of"+ps.name();
	}

	@Override
	public IClass domain() {
		return ps.domain();
	}

	@Override
	public IType range() {
		return Builtins.INTEGER;
	}

	@Override
	public String id() {
		return "count of"+ps.id();
	}

	@Override
	public String toString() {
		return "countOf("+ps.toString()+")";
	}

	@Override
	public int complexity() {
		return ps.complexity();
	}

	@Override
	public boolean multiValue() {
		return false;
	}

	@Override
	public Object getValue(Object obj) {
		Object value = ps.getValue(obj);
		if (value instanceof Collection) {
			Collection<?>z=(Collection<?>) value;
			return z.size();	
		}
		if (value==null) {
			return 0;
		}
		return 1;
	}

	@Override
	public IProperty original() {
		return ps;
	}
}