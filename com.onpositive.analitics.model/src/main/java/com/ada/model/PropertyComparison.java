package com.ada.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.clauses.IComparison;
import com.onpositive.clauses.IContext;
import com.onpositive.clauses.IHasContext;

public class PropertyComparison implements IComparison,IHasContext{

	protected final Comparative cmp;
	
	protected final IComparison comparison;
	
	protected boolean negated;

	public Comparative getCmp() {
		return cmp;
	}

	protected final IProperty prop;
	
	public IProperty getProp() {
		return prop;
	}

	public PropertyComparison(Comparative cmp, IProperty prop) {
		super();
		this.comparison=null;
		this.cmp = cmp;
		this.prop = prop;
	}
	public PropertyComparison(IComparison cmp, IProperty prop) {
		super();
		this.comparison=cmp;
		this.cmp = null;
		this.prop = prop;
	}

	@Override
	public IType domain() {
		return prop.domain();
	}

	@Override
	public IComparison negate() {
		if (this.cmp==null) {
			
			PropertyComparison propertyComparison = new PropertyComparison(this.comparison,prop);
			propertyComparison.negated=!negated;
			return propertyComparison;
			
		}
		return new PropertyComparison(new Comparative(cmp.getOperation().negate(), "not "+cmp.text),prop);
	}

	@Override
	public boolean isNotSolved() {
		return false;
	}

	@Override
	public IComparison solve(IProperty prop) {
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cmp == null) ? 0 : cmp.hashCode());
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
		PropertyComparison other = (PropertyComparison) obj;
		if (cmp == null) {
			if (other.cmp != null)
				return false;
		} else if (!cmp.equals(other.cmp))
			return false;
		if (prop == null) {
			if (other.prop != null)
				return false;
		} else if (!prop.equals(other.prop))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		if (cmp==null) {
			if (negated) {
				return "PC( not "+comparison.toString()+","+prop.toString()+")";
					
			}
			return "PC("+comparison.toString()+","+prop.toString()+")";
			
		}
		return "PC("+cmp.toString()+","+prop.toString()+")";
	}

	@Override
	public List<? extends IParsedEntity> children() {
		return Collections.emptyList();
	}

	@Override
	public List<IProperty> usedProperties() {
		return Collections.emptyList();
	}

	@Override
	public boolean match(Object property, IContext ct) {
		Object value = this.prop.getValue(property);
		if (this.cmp==null) {
			boolean innerM = innerM(ct, value);
			if (negated) {
				return !innerM;
			}
			return innerM;
		}
		return this.cmp.operation.op(value, cmp.text);
	}

	protected boolean innerM(IContext ct, Object value) {
		if (value instanceof Collection) {
			Collection mm=(Collection) value;
			for (Object z:mm) {
				if( this.comparison.match(z, ct)) {
					return true;
				}
							
			}
			return false;
		}
		if (value!=null) {
			return this.comparison.match(value, ct);
		}
		return false;
	}

	@Override
	public boolean isGoodForContainment() {
		return false;
	}

	@Override
	public void setContext(IContext ct) {
		if (prop instanceof IHasContext) {
			((IHasContext) prop).setContext(ct);
		}
	}
}
