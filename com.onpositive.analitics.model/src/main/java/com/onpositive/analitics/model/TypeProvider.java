package com.onpositive.analitics.model;

import java.util.Collection;
import java.util.HashMap;

import com.onpositive.analitics.model.java.JavaClass;

public class TypeProvider {

	static HashMap<Class<?>, IType>classes=new HashMap<>();
	
	public static IType getType(Object o) {
		Class<? extends Object> class1 = o.getClass();
		return getType(class1);		
	}
	public static IType getType(Class<? extends Object> class1) {
		
		IType iClass = classes.get(class1);
		if (iClass==null) {
			JavaClass cv=new JavaClass(class1);
			classes.put(class1, cv);
			return cv;
		}
		return iClass;
	}
	
	static {
		classes.put(boolean.class, Builtins.BOOLEAN);
		classes.put(Boolean.class, Builtins.BOOLEAN);
		classes.put(int.class, Builtins.INTEGER);
		classes.put(long.class, Builtins.INTEGER);
		classes.put(float.class, Builtins.FLOAT);
		classes.put(double.class, Builtins.FLOAT);
		classes.put(Long.class, Builtins.INTEGER);
		classes.put(Float.class, Builtins.FLOAT);
		classes.put(Double.class, Builtins.FLOAT);
		classes.put(Integer.class, Builtins.INTEGER);

		classes.put(String.class, Builtins.STRING);
	}
	public static IType getType(Collection<Object> o) {
		return null;		
	}
}
