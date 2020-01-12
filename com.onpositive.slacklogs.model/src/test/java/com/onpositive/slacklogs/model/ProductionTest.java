package com.onpositive.slacklogs.model;

import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.onpositive.analitics.model.UniverseContext;
import com.onpositive.nlp.lexer.PhrasesReplacements;
import com.onpositive.nlp.lexer.SynonimForms;
import com.onpositive.nlp.parser.AllMatchParser;

import ada.core.rules.loader.ModelLoader;
import junit.framework.TestCase;

public class ProductionTest extends TestCase{
	private UniverseContext context=new UniverseContext();

	@SuppressWarnings("unchecked")
	public ProductionTest() {
		context.add(Workspace.getInstance());
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
	
	public void test0() {
		List<Message> messages = Workspace.getInstance().messages();
		messages.forEach(v->{
			
		});
	}
}
