package com.onpositive.analitics.model.java;

import java.util.LinkedHashMap;

public class TypeResistry {

	@SuppressWarnings("rawtypes")
	protected static LinkedHashMap<Class, JavaType>tpes=new LinkedHashMap<>(); 

	
	public static JavaType getType(Class<?> object) {
		if (tpes.containsKey(object)) {
			return tpes.get(object);
		}
		JavaClass cl=new JavaClass(object);
		tpes.put(object, cl);
		return cl;
		
	}
}
