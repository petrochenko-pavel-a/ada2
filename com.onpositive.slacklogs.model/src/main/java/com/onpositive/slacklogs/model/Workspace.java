package com.onpositive.slacklogs.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.nustaq.serialization.FSTConfiguration;

import com.onpositive.analitics.model.IEntity;
import com.onpositive.analitics.model.KeyProperty;
import com.onpositive.analitics.model.Labels;
import com.onpositive.analitics.model.java.Property;
import com.onpositive.slacklogs.model.Message.Reaction;
import com.onpositive.slacklogs.model.User.ReactionEvent;

public class Workspace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Property
	ArrayList<User> users = new ArrayList<User>();

	@Property
	ArrayList<Channel> channels = new ArrayList<Channel>();
	
	
	LinkedHashMap<String,MetaReaction>reactions;

	@Labels("reaction")
	public static class MetaReaction implements IEntity{
		
		public MetaReaction(String name) {
			this.name=name;
		}

		@KeyProperty
		@Property
		@Labels("name")
		protected String name;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MetaReaction other = (MetaReaction) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		@Property
		@Labels("count")
		public int count;
	}
	
	@Property
	@Labels("reactions")
	public Collection<MetaReaction>reactions(){
		return reactions.values();
	}
	
	public static Workspace getDebugInstance() {
		if (readObject != null) {
			return readObject;
		}
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream("C:\\Users\\Павел\\git\\ada2\\com.onpositive.slacklogs.model\\store-d.dat"));
			ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
			try {
				readObject = (Workspace) objectInputStream.readObject();
				init();
				return readObject;
			} finally {
				objectInputStream.close();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static Workspace getInstance() {
		if (readObject != null) {
			return readObject;
		}
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream("C:\\Users\\Павел\\git\\ada2\\com.onpositive.slacklogs.model\\store.dat"));
			ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
			try {
				readObject = (Workspace) objectInputStream.readObject();
				init();
				return readObject;
			} finally {
				objectInputStream.close();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static void init() {
		readObject.reactions=new LinkedHashMap<>();
		readObject.users.forEach(u -> u.workspace = readObject);
		readObject.messages().forEach(m -> {
			if (m.from != null) {
				if (m.from.messages == null) {
					m.from.messages = new ArrayList<>();
				}
				m.from.messages.add(m);
			}
			if (m.reactions!=null) {
				for (Reaction r:m.reactions) {
					MetaReaction metaReaction = readObject.reactions.get(r.name);
					if (metaReaction==null) {
						MetaReaction mr=new MetaReaction(r.name);
						readObject.reactions.put(r.name, mr);
						metaReaction=mr;
					}
					metaReaction.count+=r.count;
					for (User u:r.users) {
						if (u.reactions == null) {
							u.reactions = new ArrayList<>();
						}
						u.reactions.add(new ReactionEvent(u, m, r));
					}
				}
			}
		});
	}

	static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

	private static Workspace readObject;

	public static void main(String[] args) {
		Workspace instance = getInstance();
		ArrayList<String> reactions = new ArrayList<>();
		Map<User, Integer> mmm = new HashMap<>();
		Map<User, Integer> mmm1 = new HashMap<>();

		instance.messages().stream().forEach(v -> {
			List<Reaction> reactions2 = v.reactions();
			for (Reaction c : reactions2) {
				if (c.name.contains("minus") || c.name.contains("-1") || c.name.contains("toxic")
						|| c.name.contains("heel")) {
					mmm.put(v.from, mmm.getOrDefault(v.from, 0) + c.count);
				}
			}
			// reactions.addAll(reactions2.stream().map(x->x.name).collect(Collectors.toList()));
		});
		// instance.messages().stream().forEach(v -> {
		// List<Reaction> reactions2 = v.reactions();
		// for (Reaction c : reactions2) {
		// if (c.name.contains("plus")||c.name.contains("+1")) {
		// mmm.put(v.from, mmm.getOrDefault(v.from, 0) - c.count);
		// }
		// }
		// //
		// reactions.addAll(reactions2.stream().map(x->x.name).collect(Collectors.toList()));
		// });

		Map<String, Integer> counts = reactions.parallelStream()
				.collect(Collectors.toConcurrentMap(w -> w, w -> 1, Integer::sum));
		Map<User, Integer> counts1 = sortByValue(mmm);
		Map<User, Double> counts2 = new LinkedHashMap<>();
		counts1.keySet().forEach(v -> {
			if (v != null) {
				Integer integer = counts1.get(v);
				if (integer > 100) {
					// counts2.put(v, integer/2+integer*2000 / (v.messages().size() + 0.001));}
					//
					double d = integer / (v.messages().size() + 0.001);
					if (d > 0.02 && v.messages().size() > 50) {
						System.out.println(v.name + ":" + integer + ":" + d);
					}
					// }
				}
			}
		});
		// Map<User, Double> counts3 = sortByValue(counts2);
		//
		// counts3.keySet().forEach(v -> {
		// if (v != null) {
		// Double integer = counts3.get(v);
		// System.out
		// .println(v.name + ":" + counts3.get(v));
		//
		// }
		// });
		// System.out.println(so);
		// System.out.println(sortByValue(counts));
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	@Property
	public List<Message> messages() {
		ArrayList<Message> messages = new ArrayList<>();
		this.channels.forEach(v -> messages.addAll(v.messages));
		return messages;
	}

}
