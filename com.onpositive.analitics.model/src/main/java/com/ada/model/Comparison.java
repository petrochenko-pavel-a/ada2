package com.ada.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ada.model.conditions.IHasDomain;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.clauses.IComparison;
import com.onpositive.clauses.IContext;
import com.onpositive.clauses.ISelector;
import com.onpositive.clauses.IValue;
import com.onpositive.clauses.impl.AllInstancesOf;
import com.onpositive.clauses.impl.ClauseSelector;
import com.onpositive.clauses.impl.MapByProperty;
import com.onpositive.clauses.impl.SingleSelector;

public class Comparison implements IComparison{

	protected IHasDomain comparisonTarget;
	protected Comparative comparative;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comparative == null) ? 0 : comparative.hashCode());
		result = prime * result + ((comparisonTarget == null) ? 0 : comparisonTarget.hashCode());
		result = prime * result + (notSolved ? 1231 : 1237);
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
		Comparison other = (Comparison) obj;
		if (comparative == null) {
			if (other.comparative != null)
				return false;
		} else if (!comparative.equals(other.comparative))
			return false;
		if (comparisonTarget == null) {
			if (other.comparisonTarget != null)
				return false;
		} else if (!comparisonTarget.equals(other.comparisonTarget))
			return false;
		if (notSolved != other.notSolved)
			return false;
		return true;
	}

	public Comparative getComparative() {
		return comparative;
	}

	public void setComparative(Comparative comparative) {
		this.comparative = comparative;
	}

	protected boolean notSolved;
	
	public boolean isNotSolved() {
		return notSolved;
	}

	public void setNotSolved(boolean notSolved) {
		this.notSolved = notSolved;
	}
    public Comparison solve(IProperty p){
		ISelector produce = new MapByProperty(p).produce((ISelector) comparisonTarget);
		if (produce!=null){
		return new Comparison(produce, comparative);    	
		}
		return null;
    }
	
	public Comparison(IHasDomain comparisonTarget, Comparative comparative) {
		super();
		this.comparisonTarget = comparisonTarget;
		this.comparative = comparative;
	}

	@Override
	public IType domain() {
		return comparisonTarget.domain();
	}

	@Override
	public String toString() {
		if (notSolved){
			return "NOT_SOLVED("+comparative.operation.toString()+","+comparisonTarget.toString()+")";
		}
		return "("+comparative.operation.toString()+","+comparisonTarget.toString()+")";
	}

	public Comparison negate() {
		Comparison comparison = new Comparison(comparisonTarget, new Comparative(comparative.operation.negate(), "not "+comparative.text));
		comparison.setNotSolved(notSolved);
		return comparison;
	}

	@Override
	public List<? extends IParsedEntity> children() {
		return Collections.singletonList(comparisonTarget);
	}

	@Override
	public List<IProperty> usedProperties() {
		return Collections.emptyList();
	}

	@Override
	public boolean match(Object property, IContext ct) {		
		IHasDomain comparisonTarget2 = this.comparisonTarget;
		
		if (comparisonTarget2 instanceof IValue){
			Object val=null;
			if (comparisonTarget2 instanceof ISelector){
				val=((ISelector) comparisonTarget2).values(ct).collect(Collectors.toSet());
			}
			else{
				if (comparisonTarget2 instanceof NumberInDomain){
					val=((NumberInDomain) comparisonTarget2).getNmb();
				}
				else if (comparisonTarget2 instanceof PropertyValue){
					val=((PropertyValue) comparisonTarget2).getValue();
				}
				else if (comparisonTarget2 instanceof Measure){
					Measure ssm=(Measure) comparisonTarget2;
					if (property!=null){
						ISelector selector = ssm.getSelector();
						val=ssm.amount;
						if (selector instanceof ClauseSelector){
							ClauseSelector mm=(ClauseSelector) selector;
							ClauseSelector cloneWithNewRoot = mm.cloneWithNewRoot(new SingleSelector(property, mm.domain()));
							property=cloneWithNewRoot.values(ct).collect(Collectors.toSet());
						}
						else if (selector instanceof AllInstancesOf){
							
						}
						else{
							throw new IllegalStateException("unexpected selector");
						}
						
					}
				}
			}
			if (val==null){
				throw new IllegalStateException("Not implemented yet");
			}
			return comparative.operation.op(property,val);
		}
		throw new IllegalStateException();
	}

	public IHasDomain getTarget() {
		return comparisonTarget;
	}
}
