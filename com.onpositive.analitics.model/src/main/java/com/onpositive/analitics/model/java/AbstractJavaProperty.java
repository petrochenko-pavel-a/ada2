package com.onpositive.analitics.model.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.analitics.model.Lazy;

public abstract class AbstractJavaProperty<T extends Member> implements IProperty{

	protected T member;

	private boolean lazy;
	
	public AbstractJavaProperty(T member, JavaClass domain, IType type, String name, boolean multiValue) {
		super();
		this.member = member;
		this.domain = domain;
		this.type = type;
		this.name = name;
		this.multiValue = multiValue;
		this.lazy=((AnnotatedElement)this.member).getAnnotation(Lazy.class)!=null;
		
	}

	protected JavaClass domain;
	
	protected IType type;
	
	protected String name;
	
	protected boolean multiValue;
	
	@SuppressWarnings("unchecked")
	protected Object collectionGet(Object obj) {
		LinkedHashSet<Object>result=new LinkedHashSet<>();
		@SuppressWarnings("rawtypes")
		Collection c=(Collection) obj;
		c.forEach(v->{
			Object value = getValue(v);
			if (value!=null) {
				if (value instanceof Collection) {
					result.addAll((Collection<? extends Object>) value);
				}else {
					result.add(value);
				}
			}
			
		});
		return result;
	
	}

	@Override
	public String toString() {
		return "P:"+name;
	}
	
	@Override
	public String name() {
		return name;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T annotation(Class<T> clazz) {
		if (Annotation.class.isAssignableFrom(clazz)) {
			return clazz.cast(((AnnotatedElement)this.member).getAnnotation((Class) clazz));
		}
		return null;
	}

	@Override
	public boolean isLazy() {
		return lazy;
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
