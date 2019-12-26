package com.onpositive.slacklogs.model;

import java.net.URISyntaxException;
import java.util.Collection;

import com.onpositive.analitics.model.UniverseContext;
import com.onpositive.nlp.parser.AllMatchParser;

import ada.core.rules.loader.ModelLoader;
import junit.framework.TestCase;

public class BasicTest extends TestCase {

	private UniverseContext context=new UniverseContext();

	public BasicTest() {
		context.add(Workspace.getDebugInstance());
		try {
			AllMatchParser<Object> load = ModelLoader.load(BasicTest.class.getResource("/grammar/basic.rules").toURI());
			context.init(load);
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
//	public void test1() {
//		Collection<Object> execute = context.execute("users with more then 100 messages").execute().results();
//		assertEquals(execute.size(), 20);
//	}
//	
//	public void test2() {
//		Collection<Object> execute = context.execute("users with highest number of messages").execute().results();
//		System.out.println(execute);
//		assertEquals(execute.size(), 1);
//	}
//
//	public void test3() {
//		Collection<Object> execute = context.execute("channels with messages").execute().results();
//		System.out.println(execute);
//		assertEquals(execute.size(), 3);
//	}
//	
//	public void test4() {
//		Collection<Object> execute = context.execute("users with messages in self_driving").execute().results();
//		System.out.println(execute);
//		long l0=System.currentTimeMillis();
//		execute = context.execute("users with messages in self_driving").execute().results();
//		long l1=System.currentTimeMillis();
//		System.out.println(l1-l0);
//		assertEquals(execute.size(), 170);
//	}
	
//	public void test5() {
//		Collection<Object>mm=context.execute("users with largest number of messages in self driving").execute().results();
//		for (Object z:mm) {
//			User u=(User) z;
//			if (!u.name.equals("ternaus")){
//				System.out.println(u.messages().stream().filter(x->x.channel.name.contains("driving")).count()+":"+u.name);
//			}
//		}
//		System.out.println(mm);
//	}
	
	public void test6() {
		Collection<Object>mm=context.execute("messages by channel").execute().results();
		assertEquals(mm.size(),5);
		System.out.println(mm);
	}
	
//	public void test7() {
//		Collection<Object>mm=context.execute("users reacted").execute().results();
//		assertEquals(mm.size(),4356);
//		System.out.println(mm);
//	}
	
//	public void test7() {
//		Collection<Object>mm=context.execute("users without messages").execute().results();
//		for (Object z:mm) {
//			User u=(User) z;
//			if (u.messages().size()>0) {
//				assertEquals(true, false);
//			}
//		}
//		System.out.println(mm);
//	}
	
}
