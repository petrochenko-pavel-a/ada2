package com.onpositive.analitics.model.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;

public class JavaClass extends JavaType implements IClass{

	protected LinkedHashMap<String, IProperty>properties=new LinkedHashMap<>();

	protected JavaClass superClass;
	protected Class<?>clazz;

	public JavaClass(Class<?> object) {
		Class<?> superclass2 = object.getSuperclass();
		if (superclass2!=null&&superclass2!=Object.class) {
			superClass=(JavaClass) TypeResistry.getType(superclass2);
		}
		for (Field f: object.getDeclaredFields()) {
			if (f.getAnnotation(Property.class)!=null) {
				this.properties.put(f.getName(), new FieldProperty(f, this, getType(f.getType()), f.getName(), Collection.class.isAssignableFrom(f.getType())));
			}
		}
		for (Method m: object.getDeclaredMethods()) {
			if (m.getAnnotation(Property.class)!=null) {
				this.properties.put(m.getName(), new MethodProperty(m, this, getType(m.getReturnType()), m.getName(), Collection.class.isAssignableFrom(m.getReturnType())));
			}
		}
	}

	private JavaType getType(Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOrdered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSummable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubtypeOf(IType domain) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IProperty> properties() {
		ArrayList<IProperty>res=new ArrayList<>();
		res.addAll(this.properties.values());
		return res;
	}

	@Override
	public List<IProperty> allProperties() {
		ArrayList<IProperty>res=new ArrayList<>();
		if (this.superClass!=null) {
			res.addAll(this.superClass.allProperties());
		}
		res.addAll(this.properties.values());
		return res;
	}

	@Override
	public boolean isPartOf(IClass b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<? extends IClass> contained() {
		// TODO Auto-generated method stub
		return null;
	}

	public String id() {
		return this.clazz.getName();
	}
	
}
