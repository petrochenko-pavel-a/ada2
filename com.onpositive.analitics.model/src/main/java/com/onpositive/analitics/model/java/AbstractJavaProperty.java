package com.onpositive.analitics.model.java;

import java.lang.reflect.Member;

import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;

public abstract class AbstractJavaProperty<T extends Member> implements IProperty{

	protected T member;
	
	public AbstractJavaProperty(T member, JavaClass domain, JavaType type, String name, boolean multiValue) {
		super();
		this.member = member;
		this.domain = domain;
		this.type = type;
		this.name = name;
		this.multiValue = multiValue;
	}

	protected JavaClass domain;
	
	protected JavaType type;
	
	protected String name;
	
	protected boolean multiValue;

	@Override
	public String name() {
		return name;
	}

	@Override
	public IClass domain() {
		return domain;
	}

	@Override
	public IType range() {
		return type;
	}

	@Override
	public String id() {
		return this.domain.id()+"."+this.name();
	}

	@Override
	public int complexity() {
		return 0;
	}

	@Override
	public boolean multiValue() {
		return multiValue;
	}

	
}
