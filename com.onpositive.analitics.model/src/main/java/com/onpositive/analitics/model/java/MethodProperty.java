package com.onpositive.analitics.model.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.onpositive.analitics.model.IType;

public class MethodProperty extends AbstractJavaProperty<Method>{

	public MethodProperty(Method member, JavaClass domain, IType type, String name, boolean multiValue) {
		super(member, domain, type, name, multiValue);
	}

	@Override
	public Object getValue(Object obj) {
		try {
			return member.invoke(obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException();
		}
	}

}
