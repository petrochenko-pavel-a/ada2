package com.ada.model.conditions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ada.model.IParsedEntity;
import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;

public interface IHasDomain extends IParsedEntity{

	IType domain();

	default IType originalDomain(){
		return domain();
	}
	
	default Optional<IClass>clazz(){
		if (domain() instanceof IClass){
			return Optional.of((IClass)domain());
		}
		return Optional.empty();
	}
	
	default List<IProperty>properties(){
		return clazz().map(x->x.allProperties()).orElse(Collections.emptyList());
	}
}
