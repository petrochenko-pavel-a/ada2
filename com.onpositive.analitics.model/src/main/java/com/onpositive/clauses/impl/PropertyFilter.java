package com.onpositive.clauses.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ada.model.IParsedEntity;
import com.ada.model.conditions.IHasDomain;
import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.clauses.IClause;
import com.onpositive.clauses.IComparison;
import com.onpositive.clauses.IContext;
import com.onpositive.clauses.IHasContext;
import com.onpositive.clauses.ISelector;

public class PropertyFilter implements IClause,IHasDomain,IProperty,IHasContext {

	protected final IProperty prop;
	protected boolean inverse;
	protected boolean tkFirst;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((prop == null) ? 0 : prop.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyFilter other = (PropertyFilter) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (prop == null) {
			if (other.prop != null)
				return false;
		} else if (!prop.equals(other.prop))
			return false;
		return true;
	}

	protected IComparison predicate;
	
	public void setPredicate(IComparison predicate) {
		this.predicate = predicate;
	}

	public IComparison getPredicate() {
		return predicate;
	}

	private PropertyFilter(IProperty prop, IComparison predicate) {
		super();
		this.prop = prop;
		this.predicate = predicate;
	} 
	
	public IProperty property(){
		return prop;		
	}
	
	@Override
	public String toString() {
		if (this.inverse) {
			return "FILTER(!"+prop.toString()+","+predicate.toString()+")";
			
		}
		return "FILTER("+prop.toString()+","+predicate.toString()+")";
	}

	@Clause("FILTER")
	public static PropertyFilter propertyFilter(IProperty prop, IComparison predicate) {
		if (prop instanceof PropertyFilter||prop ==null) {
			return null;
		}
		if (predicate.domain().isSubtypeOf(prop.domain())){
			prop=InverseProperty.createInverseProperty(prop);
		}		
		return new PropertyFilter(prop, predicate);
	}
	

	@Override
	public ISelector produce(ISelector s) {
		if (!s.domain().isSubtypeOf(prop.domain())) {
			return null;
		}
		return ClauseSelector.produce(s, s.domain(), null, this);
	}

	@Override
	public IClass domain() {
		return prop.domain();
	}

	public IHasDomain negate() {
		PropertyFilter propertyFilter = new PropertyFilter(prop, predicate);
		propertyFilter.inverse=!this.inverse;
		return propertyFilter;
	}

	@Override
	public List<? extends IParsedEntity> children() {
		return Collections.singletonList(predicate);
	}

	@Override
	public List<IProperty> usedProperties() {
		return Collections.singletonList(prop);
	}

	@Override
	public Stream<Object> perform(Stream<Object> selector, IContext ct) {		
		return selector.filter(x->isOk(ct, x));
	}

	private boolean isOk(IContext ct, Object x) {
		if (this.inverse) {
			return !predicate.match(prop.getValue(x),ct);
		}
		return predicate.match(prop.getValue(x),ct);
	}

	@Override
	public String name() {
		return toString();
	}

	@Override
	public IType range() {
		return prop.domain();
	}

	@Override
	public String id() {
		return toString();
	}

	@Override
	public int complexity() {
		return 0;
	}

	@Override
	public boolean multiValue() {
		return prop.multiValue();
	}
	
	IContext ct;
	
	@Override
	public Object getValue(Object obj) {
		if (obj instanceof Collection) {
			 return perform(((Collection)obj).stream(),ct).collect(Collectors.toSet());
		}
		Set<Object> collect = perform(Collections.singleton(obj).stream(),ct).collect(Collectors.toSet());
		return collect;
	}

	

	@Override
	public void setContext(IContext ct) {
		this.ct=ct;
	}	
	@Override
	public boolean canMap() {
		return this.prop.canMap();
	}
}