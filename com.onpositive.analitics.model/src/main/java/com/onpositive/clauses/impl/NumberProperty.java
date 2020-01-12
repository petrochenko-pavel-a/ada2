package com.onpositive.clauses.impl;

import java.util.Collection;

import com.onpositive.analitics.model.Builtins;
import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.ICompoundProperty;
import com.onpositive.analitics.model.ICounter;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.analitics.model.java.Counter;
import com.onpositive.clauses.IContext;
import com.onpositive.clauses.IHasContext;

public class NumberProperty implements IProperty, ICompoundProperty,IHasContext {

	public final IProperty ps;
	private ICounter counter;

	public NumberProperty(IProperty ps) {
		super();
		this.ps = ps;
		IType range = ps.range();
		if (range instanceof IClass) {
			Counter annotation = range.annotation(Counter.class);
			if (annotation != null) {
				try {
					ICounter ck = annotation.value().newInstance();
					this.counter = ck;
				} catch (InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String name() {
		return "count of" + ps.name();
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
		return "count of" + ps.id();
	}

	@Override
	public String toString() {
		return "countOf(" + ps.toString() + ")";
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
			Collection<?> z = (Collection<?>) value;
			if (counter != null) {
				int res=0;
				for (Object o : z) {
					res=res+counter.count(o);
				}
				return res;
			}
			return z.size();
		}
		if (value == null) {
			return 0;
		}
		if (counter!=null) {
			return counter.count(value);
		}
		return 1;
	}

	@Override
	public IProperty original() {
		return ps;
	}

	@Override
	public void setContext(IContext ct) {
		if (ps instanceof IHasContext) {
			((IHasContext) ps).setContext(ct);
		}
	}
}