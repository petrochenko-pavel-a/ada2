package com.onpositive.clauses;

import com.ada.model.conditions.IHasDomain;
import com.onpositive.analitics.model.IProperty;

public interface IComparison extends IHasDomain{

	IComparison negate();

	boolean isNotSolved();

	IComparison solve(IProperty prop);

	boolean match(Object property, IContext ct);

}
