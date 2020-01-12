package com.onpositive.analitics.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface IClass extends IType{
 
	List<IProperty>properties();

	List<IProperty>allProperties();
	
	default Optional<IProperty> property(String id) {
        return allProperties().stream().filter(x->x.name().equals(id)).findFirst();
    }
	
	boolean isPartOf(IClass b);
	
	Collection<? extends IClass>contained();

	IProperty keyProperty();
	
	BiPredicate<Object, Object>containing(IClass c);
}
