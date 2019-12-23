package com.onpositive.analitics.model;

import java.util.Collection;

import org.junit.Test;

import com.onpositive.analitics.model.java.JavaClass;
import com.onpositive.analitics.model.java.Universe;

import junit.framework.TestCase;

public class UniverseTest extends TestCase{

	@Test
	public void test() {
		Universe uv=new Universe();
		uv.add(TypeProvider.getType(JavaClass.class));
		Collection<IClass> classes = uv.classes();
		System.out.println(classes.size());
//		fail("Not yet implemented");
	}

}
