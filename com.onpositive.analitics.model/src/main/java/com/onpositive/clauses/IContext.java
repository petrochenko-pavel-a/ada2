package com.onpositive.clauses;

import java.util.stream.Stream;

import com.onpositive.analitics.model.IType;

public interface IContext {

	ITypedStore store();

	Stream<Object> allInstancesOf(IType tp);

}