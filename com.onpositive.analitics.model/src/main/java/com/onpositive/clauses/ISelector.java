package com.onpositive.clauses;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.clauses.impl.AndSelector;
import com.onpositive.clauses.impl.MapByProperty;
import com.onpositive.clauses.impl.OrSelector;

public interface ISelector extends IValue{
	
	IType domain(); 
	
	Multiplicity multiplicity();
	
	default ISelector map(IProperty pr){
		return MapByProperty.map(pr).produce(this);
	}
	default ISelector map(String pr){
		return map(clazz().get().property(pr).get());		
	}

	default ISelector or(ISelector pr){
		return new OrSelector(new LinkedHashSet<>(Arrays.asList(new ISelector[]{this,pr})));
	}
	default ISelector and(ISelector pr){
		return new AndSelector(new LinkedHashSet<>(Arrays.asList(new ISelector[]{this,pr})));
	}
	
	public default ISelector optimize() {
		return this;
	}
	
	default List<IProperty>properties(){
		IType domain = domain();
		if (domain instanceof IClass){
			return ((IClass) domain).properties();
		}
		return Collections.emptyList();
	}
	
	Stream<Object>values(IContext ct);

	 default boolean mappable() {
		 return true;
	 }
	
}