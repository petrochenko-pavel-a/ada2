package com.onpositive.slacklogs.model;

import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.yaml.snakeyaml.Yaml;
import com.onpositive.analitics.model.UniverseContext;
import com.onpositive.nlp.lexer.PhrasesReplacements;
import com.onpositive.nlp.lexer.SynonimForms;
import com.onpositive.nlp.parser.AllMatchParser;
import com.onpositive.slacklogs.model.Message.Reaction;
import com.onpositive.slacklogs.model.User.ReactionEvent;

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
				
		assertTrue(execute.size()==2);		
		execute = context.execute("каналы в которых есть сообщения Alex Natekin").execute().results();	
		assertTrue(execute.size()==2);
		Collection<Object> execute2 = context.execute("сообщения Alex Natekin").execute().results();	
		assertTrue(execute2.size()==294);
		execute2 = context.execute("сообщения написанные Alex Natekin").execute().results();	
		assertTrue(execute2.size()==294);
		execute2 = context.execute("сообщения написанные Alex Natekin в self driving").execute().results();	
		assertTrue(execute2.size()==1);
		execute2 = context.execute("сообщения Alex Natekin за 2019 год").execute().results();	
		assertTrue(execute2.size()==4);
	}
	public void test9() {
		Collection<Object> execute = context.execute("сколько сообщений в self driving").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("2487.0"));
		execute = context.execute("число сообщений в self driving").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("2487.0"));
		execute = context.execute("сколько сообщений написал Natekin").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("294.0"));
	}
	public void test10() {
		Collection<Object> execute = context.execute("кто написал больше всего сообщений").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("Roman Degtiarev"));
		execute = context.execute("пользователь с наибольшим количеством сообщений").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("Roman Degtiarev"));
		execute = context.execute("пользователь с наибольшим количеством сообщений в  2014-2019 годах").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("Roman Degtiarev"));
		execute = context.execute("пользователь с наибольшим количеством сообщений в  2014-2019 годах в канале self driving").execute().results();
		assertTrue(execute.iterator().next().toString().equals("Vladimir Iglovikov"));		
	}
	
	public void test11() {
		Collection<Object> execute = context.execute("кто написал больше всего сообщений").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("Roman Degtiarev"));
		
		execute = context.execute("пользователь с наибольшим количеством сообщений").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("Roman Degtiarev"));
		execute = context.execute("пользователь с наибольшим количеством сообщений в  2014-2019 годах").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("Roman Degtiarev"));
//		execute = context.execute("пользователь с наибольшим количеством сообщений в  2014-2019 годах в канале self driving").execute().results();
//		assertTrue(execute.iterator().next().toString().equals("Vladimir Iglovikov"));
		
		execute = context.execute("кто больше всех писал в self driving").execute().results();	
		assertTrue(execute.iterator().next().toString().equals("Vladimir Iglovikov"));
	}	
	
	public void test13() {
		Collection<Object> execute = context.execute("сообщения с reactions").execute().results();
		assertEquals(execute.size(), 2680);
		execute = context.execute("сообщения без reaction").execute().results();
		assertEquals(execute.size(), 10289);
		execute = context.execute("сообщения с наибольшим количеством reactions").execute().results();
		assertEquals(execute.size(), 1);
		execute = context.execute("5 сообщений с наибольшим количеством reactions").execute().results();
		assertEquals(execute.size(), 5);
		execute = context.execute("5 сообщений с максимальным количеством reactions").execute().results();
		assertEquals(execute.size(), 5);
		execute = context.execute("5 сообщений с максимальным количеством reactions").execute().results();
		assertEquals(execute.size(), 5);
		execute = context.execute("сообщения с максимальным количеством reactions в 2015 году").execute().results();
		assertEquals(execute.size(), 1);
		Collection<Object> execute2 = context.execute("сообщения с максимальным количеством reactions до 2019 году").execute().results();
		assertEquals(execute2.size(), 1);
		Collection<Object> execute3 = context.execute("сообщения в self driving с максимальным количеством reactions в 2019 году").execute().results();
		assertEquals(execute3.size(), 1);
		Collection<Object> execute4 = context.execute("сообщения с максимальным количеством reactions в 2019 году в self driving").execute().results();
		assertEquals(execute4.size(), 1);
	}
	public void test14() {
		Collection<Object> execute = context.execute("сообщения с ban").execute().results();
		assertEquals(execute.size(), 12);
		execute = context.execute("сообщения с ban в канале self driving").execute().results();
		assertEquals(execute.size(), 2);
		execute = context.execute("сообщения с ban или toxic").execute().results();
		assertEquals(execute.size(), 14);
		execute = context.execute("сообщения с ban или toxic в канале self driving").execute().results();
		assertEquals(execute.size(), 3);
		execute = context.execute("сообщения с ban и alex в канале self driving").execute().results();
		assertEquals(execute.size(), 1);
		execute = context.execute("сообщения с ban в 2016 году").execute().results();
		assertEquals(execute.size(), 1);
	}
	public void test15() {
		//Collection<Object> execute = context.execute("Кто set ban").execute().results();
//		Collection<Object> execute = context.execute("Кто ставил reactions в self driving").execute().results();
//		assertTrue(execute.size()>10&&execute.size()<1000);
//		System.out.println(execute.size()>0);
//		execute = context.execute("Кто больше всех ставил reactions в self driving").execute().results();
//		assertTrue(execute.iterator().next().toString().equals("Vladimir Iglovikov"));
//		System.out.println(execute.size()>0);
		User mm=(User) context.execute("Vladimir Iglovikov").execute().results().iterator().next();
		HashMap<User, Integer>map=new HashMap<>();
		for (Message z:mm.messages()) {
			List<Reaction> reactions = z.reactions();
			for (Reaction q:reactions) {
				for (User u:q.users) {
					Integer integer = map.get(u);
					if (integer==null) {
						integer=0;
					}
					map.put(u, integer+1);
				}
			}
		}
		User maxUser=null;
		int maxCount=0;
		for (User un:map.keySet()) {
			Integer integer = map.get(un);
			if (integer>maxCount) {
				maxUser=un;
				maxCount=integer;
			}
		}
		Collection<Object> execute = context.execute("Кто больше всех ставил reactions в сообщения Vladimir Iglovikov").execute().results();
		assertTrue(execute.iterator().next().equals(maxUser));
		
		
		execute = context.execute("Кто больше всех ставил reactions в Vladimir Iglovikov").execute().results();
		assertTrue(execute.iterator().next().equals(maxUser));
		execute = context.execute("Кто больше всех ставил reactions в сообщения от Vladimir Iglovikov").execute().results();
		assertTrue(execute.iterator().next().equals(maxUser));
		execute = context.execute("Кто больше всех ставил reactions на сообщения Vladimir Iglovikov").execute().results();
		assertTrue(execute.iterator().next().equals(maxUser));
		execute = context.execute("Кто больше всех ставил reactions для Vladimir Iglovikov").execute().results();
		assertTrue(execute.iterator().next().equals(maxUser));
	
		execute = context.execute("Кому больше всех ставил reactions  Vladimir Iglovikov").execute().results();
		map=new HashMap<>();		
		for (ReactionEvent e:mm.reactions) {
			
			Integer integer = map.get(e.message_author);
			if (integer==null) {
				integer=0;
			}
			map.put(e.message_author, integer+1);
		}
		maxUser=null;
		maxCount=0;
		for (User un:map.keySet()) {
			Integer integer = map.get(un);
			if (integer>maxCount) {
				maxUser=un;
				maxCount=integer;
			}
		}
		execute = context.execute("Кому Vladimir Iglovikov ставил reactions").execute().results();
		//assertTrue(execute.iterator().next().equals(maxUser));
//		System.out.println(execute);
//		
//		execute = context.execute("Кому больше всех ставил reactions Vladimir Iglovikov").execute().results();
//		System.out.println(execute);
		//assertTrue(execute.iterator().next().toString().equals("Vladimir Iglovikov"));
//		execute = context.execute("Кто ставил ban Vladimir Iglovikov").execute().results();
//		System.out.println(execute);
	}

	public void test16() {
		Collection<Object> execute = context.execute("Кому Vladimir Iglovikov ставил reactions").execute().results();
		assertTrue(execute.size()==65);
		Collection<Object> execute2 = context.execute("Кому Vladimir Iglovikov больше всех ставил").execute().results();
		System.out.println(execute2);
		//assertTrue(execute.iterator().next().equals(maxUser));
	}
}