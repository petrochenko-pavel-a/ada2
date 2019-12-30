package com.onpositive.clauses.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import com.ada.model.IParsedEntity;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.clauses.IClause;
import com.onpositive.clauses.IContext;
import com.onpositive.clauses.ISelector;

public class SortByClause implements IClause{

	protected IProperty ps;
	
	public SortByClause(IProperty ps) {
		super();
		this.ps = ps;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IParsedEntity> children() {
		return Collections.singletonList(ps);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IProperty> usedProperties() {
		return Collections.singletonList(ps);
	}

	@Override
	public ISelector produce(ISelector s) {
		return ClauseSelector.produce(s, s.domain(), s.multiplicity(), this);
	}

	@Override
	public Stream<Object> perform(Stream<Object> selector, IContext ct) {
		return selector.sorted(new Comparator<Object>() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public int compare(Object o1, Object o2) {
				Comparable om0=(Comparable) ps.getValue(o1);
				Comparable om1=(Comparable) ps.getValue(o2);
				if (om0==null) {
					if (om1==null) {
						return 0;
					}
					return 1;
				}
				if (om1==null) {
					
					return -1;
				}
				try {
				return om0.compareTo(om1);
				}catch (Exception e) {
					return om0.toString().compareTo(om1.toString());
				}
			}
		});
	}
	@Override
	public String toString() {
		return "sort_by("+ps+")";
	}

}
