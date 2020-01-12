package com.onpositive.slacklogs.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.onpositive.analitics.model.ActionNames;
import com.onpositive.analitics.model.Containment;
import com.onpositive.analitics.model.IEntity;
import com.onpositive.analitics.model.KeyProperty;
import com.onpositive.analitics.model.Labels;
import com.onpositive.analitics.model.Lazy;
import com.onpositive.analitics.model.QuestionNames;
import com.onpositive.analitics.model.java.InverseOf;
import com.onpositive.analitics.model.java.Property;
import com.onpositive.nlp.lexer.IUncountable;
import com.onpositive.slacklogs.model.Message.Reaction;

@Labels({"user","member","член сообщества","пользователь","чувак","человек","люди","пацан","мужчина","женщина"})
@QuestionNames(directForm= {"кто","те кто"},inverseForm= {"кому","те кому"})
public class User implements Serializable,IEntity,IUncountable{

	private String id;

	public User(String id) {
		this.setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return this.real_name;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Property
	protected String name;
	
	@Property
	@KeyProperty
	protected String real_name;
	
	protected String url;
	
	transient Workspace workspace;

	protected List<Message>messages;
	
	protected List<ReactionEvent>reactions;
	

	@Property
	@Labels("reactions")
	
	public List<Reaction>reactions(){
		ArrayList<Reaction>rs=new ArrayList<>();
		this.messages().forEach(v->{
			rs.addAll(v.reactions());
		});
		return rs;		
	}
	
	@Labels("reactions")
	public static class ReactionEvent {
		
		
		@Property
		@Containment
		protected User message_author;
		
		@Property
		
		protected User reaction_author;
		
		public ReactionEvent(User author, Message message, Reaction reaction) {
			this.message_author = message.from;
			this.reaction_author=author;
			this.message = message;
			this.reaction = reaction;
		}
		@Property
		protected Message message;
		
		protected Reaction reaction;
		
		
		@Property
		public Channel channel() {
			return message.channel;
		}		
	}
	
	@Property
	@Labels("ressacted")
	@ActionNames("set")
	@InverseOf("author")
	public List<ReactionEvent>reacted(){
		if (this.reactions==null) {
			return Collections.emptyList();
		}
		return this.reactions;		
	}
	
	public LocalDateTime date() {
		LocalDateTime ld=null;
		for (Message m:this.messages()) {
			LocalDateTime date = m.date();
			if (ld==null) {
				ld=date;
			}
			else {
				if (date.isBefore(ld)) {
					ld=date;
				}
			}
		}
		return ld;
	}
	
	@Property
	@Labels({"messages","сообщений","сообщения"})
	@ActionNames({"write","create"})
	@InverseOf("from")
	@Lazy
	public List<Message> messages() {
		if (messages==null) {
			return Collections.emptyList();
		}
		return messages;
	}
	
}