package com.ada.model;

import java.util.Collections;
import java.util.List;

import com.onpositive.analitics.model.Builtins;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.parsers.dates.IFreeFormDate;

public class GenericTime implements IScalarWithDimension{

	protected IFreeFormDate part;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((part == null) ? 0 : part.hashCode());
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
		GenericTime other = (GenericTime) obj;
		if (part == null) {
			if (other.part != null)
				return false;
		} else if (!part.equals(other.part))
			return false;
		return true;
	}

	public GenericTime(IFreeFormDate part) {
		super();
		this.part = part;
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
	public IType domain() {
		return Builtins.DATETIME;
	}
	
	@Override
	public String toString() {		
		return part.toString();
	}

	public IFreeFormDate time() {
		return part;
	}



}
