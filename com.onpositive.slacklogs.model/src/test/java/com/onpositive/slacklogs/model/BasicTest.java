package com.onpositive.slacklogs.model;

import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.onpositive.analitics.model.UniverseContext;
import com.onpositive.nlp.lexer.PhrasesReplacements;
import com.onpositive.nlp.lexer.SynonimForms;
import com.onpositive.nlp.parser.AllMatchParser;

import ada.core.rules.loader.ModelLoader;
import junit.framework.TestCase;

public class BasicTest extends TestCase {

	private UniverseContext context=new UniverseContext();

	@SuppressWarnings("unchecked")
	public BasicTest() {
		context.add(Workspace.getDebugInstance());
		try {
			AllMatchParser<Object> load = ModelLoader.load(BasicTest.class.getResource("/grammar/basic.rules").toURI());
			context.init(load);
			@SuppressWarnings("rawtypes")
			LinkedHashMap loadAs = new Yaml().loadAs(new InputStreamReader(BasicTest.class.getResourceAsStream("/synonims.yaml")),LinkedHashMap.class);
			loadAs.keySet().forEach(v->{
				List<String>sm=(List<String>) loadAs.get(v);
				for (String c:sm) {
					SynonimForms.register(v.toString(),c);
				}
			});
			LinkedHashMap loadAs2= new Yaml().loadAs(new InputStreamReader(BasicTest.class.getResourceAsStream("/replacements.yaml")),LinkedHashMap.class);
			loadAs2.keySet().forEach(v->{
					PhrasesReplacements.replacement(v.toString(),(String) loadAs2.get(v));
				}
			);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void test1() {
		Collection<Object> execute = context.execute("люди с более чем 100 сообщениями").execute().results();
		assertEquals(execute.size(), 20);
		execute = context.execute("кто написал более чем 100 сообщений").execute().results();
		assertEquals(execute.size(), 20);
		execute = context.execute("кто написала более чем 100 сообщений").execute().results();
		assertEquals(execute.size(), 20);
		execute = context.execute("кто запостил более 100 сообщений").execute().results();
		assertEquals(execute.size(), 20);
		execute = context.execute("У кого больше чем 100 сообщений").execute().results();		
		assertEquals(execute.size(), 20);		
		execute = context.execute("У кого больше 100 сообщений").execute().results();		
		assertEquals(execute.size(), 20);		
	}
	public void test2() {
		Collection<Object> execute = context.execute("Кто писал в self driving").execute().results();	
		assertEquals(execute.size(), 170);
		execute = context.execute("Кто не писал в self driving").execute().results();	
		assertTrue(execute.size()> 1000);
		execute = context.execute("люди которые писали в self driving").execute().results();	
		assertTrue(execute.size()==170);
		execute = context.execute("Те кто писали в self driving").execute().results();	
		assertTrue(execute.size()== 170);
	}
	public void test3() {
		Collection<Object> execute = context.execute("Люди с наибольшим количеством of сообщений").execute().results();	
		assertEquals(execute.size(), 1);
		execute = context.execute("Люди с наибольшим количеством of сообщений в self driving").execute().results();	
		assertEquals(execute.size(), 1);
		Collection<Object> execute1 = context.execute("Кто больше всего пишет в self driving").execute().results();	
		assertEquals(execute1.size(), 1);
	}
	
	public void test4() {
		Collection<Object> execute = context.execute("Пользователи без сообщений").execute().results();	
		assertTrue(execute.size()>40000);
		for (Object o:execute) {
			User m=(User) o;
			if (m.messages().size()>0) {
				assertTrue(false);
			}
		}
	}
//	
	public void test5() {
		Collection<Object> execute = context.execute("Пользователи с сообщениями в 2015 году").execute().results();	
		assertTrue(execute.size()==27);
	}
	public void test6() {
		Collection<Object> execute = context.execute("Пользователи с сообщениями до 2016 года").execute().results();	
		assertTrue(execute.size()==27);
		execute = context.execute("Кто писал в self driving в 2018 году").execute().results();	
		assertTrue(execute.size()==90);
		execute = context.execute("Кто писал в self driving после 2018 года").execute().results();	
		assertTrue(execute.size()==121);
		Collection<Object> execute1 = context.execute("Кто писал в self driving после 2018 года и  писал в 2016 году").execute().results();	
		assertTrue(execute1.size()==8);
		Collection<Object> execute2 = context.execute("Кто писал в self driving после 2018 года и не писал в self driving до 2019 года").execute().results();	
		for (Object a:execute2) {
			User m=(User) a;
			m.messages().stream().filter(x->x.channel.name.contains("driving")).forEach(x->{
				System.out.println(x.from+":"+x.date()+":"+m.date());
			});
		}
		assertEquals(execute2.size(), 69);
		Collection<Object> execute3 = context.execute("Кто писал в self driving после 2018 года и  писал  до 2019 года").execute().results();	
		assertEquals(execute3.size(), 52);
	}
	

	public void test7() {
		Collection<Object> execute = context.execute("первое сообщение в self driving").execute().results();	
		assertTrue(execute.size()==1);
		Message m=(Message) execute.iterator().next();
		System.out.println(m.date());
		execute = context.execute("последнее сообщение в self driving").execute().results();	
		m=(Message) execute.iterator().next();
		System.out.println(m.date());
		execute = context.execute("сообщения в self driving за ноябрь 2019 года").execute().results();	
		assertEquals(execute.size(), 3);
		execute = context.execute("последние 10 сообщений в self driving").execute().results();	
		assertEquals(execute.size(), 10);
		m=(Message) execute.iterator().next();
		System.out.println(m.date());
	}
	public void test8() {
		Collection<Object> execute = context.execute("сообщения от Alex Natekin").execute().results();	
		assertTrue(execute.size()==294);
		execute = context.execute("каналы с сообщениями от Alex Natekin").execute().results();	
		//assertTrue(execute.size()==1);

		execute = context.execute("каналы в которых есть сообщения Alex Natekin").execute().results();	
		assertTrue(execute.size()==2);
		execute = context.execute("каналы с сообщениями Alex Natekin").execute().results();	
		assertTrue(execute.size()==2);
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
