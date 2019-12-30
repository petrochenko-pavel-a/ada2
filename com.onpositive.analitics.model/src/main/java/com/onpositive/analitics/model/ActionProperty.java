package com.onpositive.analitics.model;

public class ActionProperty implements IProperty{

	protected IProperty parent;
	private String text;

	public ActionProperty(IProperty parent,String text) {
		super();
		this.parent = parent;
		this.text=text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
		ActionProperty other = (ActionProperty) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	public String name() {
		return parent.name();
	}

	public IClass domain() {
		return parent.domain();
	}

	public IType range() {
		return parent.range();
	}

	public String id() {
		return parent.id();
	}

	public int complexity() {
		return parent.complexity();
	}

	public boolean hasCompatibleRange(IType t) {
		return parent.hasCompatibleRange(t);
	}

	public boolean multiValue() {
		return parent.multiValue();
	}

	public Object getValue(Object obj) {
		return parent.getValue(obj);
	}

	public  <T> T annotation(Class<T> clazz) {
		return parent.annotation(clazz);
	}

	public  boolean isLazy() {
		return parent.isLazy();
	}
	
	@Override
	public String toString() {
		return "PA:"+text;
	}
}
