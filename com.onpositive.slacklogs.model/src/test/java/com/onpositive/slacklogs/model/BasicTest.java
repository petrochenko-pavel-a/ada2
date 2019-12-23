package com.onpositive.slacklogs.model;

import java.net.URISyntaxException;

import com.onpositive.analitics.model.TypeProvider;
import com.onpositive.analitics.model.UniverseContext;
import com.onpositive.clauses.impl.AllInstancesOf;
import com.onpositive.nlp.parser.AllMatchParser;

import ada.core.rules.loader.ModelLoader;
import junit.framework.TestCase;

public class BasicTest extends TestCase {

	public void test0() {
		UniverseContext cm=new UniverseContext();
		cm.add(Workspace.getInstance());
		System.out.println(cm.size());
		System.out.println(cm.types());
	}
	
	public void test1() {
		UniverseContext cm=new UniverseContext();
		cm.add(Workspace.getInstance());
		
		AllInstancesOf allInstancesOf = new AllInstancesOf(TypeProvider.getType(User.class));
		try {
			AllMatchParser<Object> load = ModelLoader.load(BasicTest.class.getResource("/grammar/basic.rules").toURI());
			System.out.println(load);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(cm.execute(allInstancesOf).size());
	}
}
