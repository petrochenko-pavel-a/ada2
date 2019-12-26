package com.onpositive.analitics.model.java;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.onpositive.analitics.model.IType;

public class FieldProperty extends AbstractJavaProperty<Field>{

	public FieldProperty(Field member, JavaClass domain, IType type, String name, boolean multiValue) {
		super(member, domain, type, name, multiValue);
		member.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(Object obj) {
		try {
			if (obj instanceof Collection) {
				return collectionGet(obj);
			}
			return member.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException();
		}
	}

	

}
