package com.onpositive.analitics.model;

public interface IProperty extends INamed{
	
	IClass domain();
	
	IType range();
	
	String id();
	
	int complexity();
	
	default boolean hasCompatibleRange(IType t){
		return this.range().isSubtypeOf(t);
	}

	boolean multiValue();

	public Object getValue(Object obj);

	default <T> T annotation(Class<T>clazz) {
		return null;
	}

	default boolean isLazy() { return false;}
	
	default boolean eq(IProperty z) {
		if (this.equals(z)) {
			return true;
		}
		if (z.equals(this)) {
			return true;
		}
		return false;
	}
	
	default boolean canMap() {
		return true;
	}
}
