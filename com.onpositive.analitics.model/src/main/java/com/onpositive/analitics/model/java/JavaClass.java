package com.onpositive.analitics.model.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiPredicate;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.analitics.model.KeyProperty;
import com.onpositive.analitics.model.TypeProvider;

public class JavaClass extends JavaType implements IClass{

	protected LinkedHashMap<String, IProperty>properties=new LinkedHashMap<>();

	protected JavaClass superClass;
	protected Class<?>clazz;
	
	protected LinkedHashSet<JavaClass>subClasses=new LinkedHashSet<>();

	private IProperty keyProperty;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> T annotation(Class<T> clazz) {
		if (Annotation.class.isAssignableFrom(clazz)) {
			return clazz.cast(this.clazz.getAnnotation((Class) clazz));
		}
		return super.annotation(clazz);
	}
	
	public JavaClass(Class<?> object) {
		Class<?> superclass2 = object.getSuperclass();
		if (superclass2!=null&&superclass2!=Object.class) {
			superClass=(JavaClass) TypeResistry.getType(superclass2);
			superClass.subClasses.add(this);
		}
		this.clazz=object;
		TypeProvider.register(object,this);
		for (Field f: object.getDeclaredFields()) {
			if (f.getAnnotation(Property.class)!=null) {
				this.properties.put(f.getName(), new FieldProperty(f, this, getType(f.getGenericType()), f.getName(), Collection.class.isAssignableFrom(f.getType())));
			}
		}
		for (Method m: object.getDeclaredMethods()) {
			if (m.getAnnotation(Property.class)!=null) {
				this.properties.put(m.getName(), new MethodProperty(m, this, getType(m.getGenericReturnType()), m.getName(), Collection.class.isAssignableFrom(m.getReturnType())));
			}
		}
		for (IProperty p:this.allProperties()) {
			if (p.annotation(KeyProperty.class)!=null) {
				this.keyProperty=p;
			}
		}
		if (INoKey.class.isAssignableFrom(object)) {
			this.keyProperty=null;
		}
	}

	private IType getType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType tp=(ParameterizedType) type;
			Type rawType = tp.getRawType();
			if (rawType instanceof Class) {
				if (Collection.class.isAssignableFrom((Class<?>) rawType)){
					return (JavaType) TypeProvider.getType((Class)tp.getActualTypeArguments()[0]);
				}
			}
		}
		return  TypeProvider.getType((Class)type);
	}
	
	@Override
	public String toString() {
		return this.clazz.getName();
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
		if (this.equals(domain)) {
			return true;
		}
		if (this.superClass!=null&&this.superClass.equals(domain)) {
			return true;
		}
		return false;
	}

	@Override
	public String name() {
		return this.clazz.getSimpleName();
	}

	@Override
	@Property
	public List<IProperty> properties() {
		ArrayList<IProperty>res=new ArrayList<>();
		res.addAll(this.properties.values());
		return res;
	}

	@Override
	@Property
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

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends IClass> contained() {
		return Collections.emptyList();
	}

	public String id() {
		return this.clazz.getName();
	}

	@Override
	public IProperty keyProperty() {
		return keyProperty;
	}

	@Override
	public BiPredicate<Object, Object> containing(IClass c) {
		return null;
	}
	
}
