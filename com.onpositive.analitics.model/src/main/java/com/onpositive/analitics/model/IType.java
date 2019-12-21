package com.onpositive.analitics.model;

public interface IType extends INamed{

	boolean isOrdered();

	boolean isSummable();

	boolean isSubtypeOf(IType domain);

}
