package com.onpositive.analitics.model.java;

import java.lang.reflect.Field;

public class FieldProperty extends AbstractJavaProperty<Field>{

	public FieldProperty(Field member, JavaClass domain, JavaType type, String name, boolean multiValue) {
		super(member, domain, type, name, multiValue);
	}

	@Override
	public Object getValue(Object obj) {
		try {
			return member.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException();
		}
	}

}
