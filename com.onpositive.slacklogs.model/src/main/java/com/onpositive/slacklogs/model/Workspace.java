package com.onpositive.slacklogs.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.nustaq.serialization.FSTConfiguration;

import com.onpositive.slacklogs.model.Message.Reaction;

public class Workspace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<User> users = new ArrayList<User>();
	ArrayList<Channel> channels = new ArrayList<Channel>();

	public static Workspace getInstance() {
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream("/Users/kor/git/ada2/com.onpositive.slacklogs.model/store.dat"));
			ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
			try {
				Workspace readObject = (Workspace) objectInputStream.readObject();
				readObject.users.forEach(u -> u.workspace = readObject);
				return readObject;
			} finally {
				objectInputStream.close();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

	public static void main(String[] args) {
		Workspace instance = getInstance();
		ArrayList<String> reactions = new ArrayList<>();
		Map<User, Integer> mmm = new HashMap<>();
		instance.messages().stream().forEach(v -> {
			List<Reaction> reactions2 = v.reactions();
			for (Reaction c : reactions2) {
				if (c.name.contains("plus")) {
					mmm.put(v.from, mmm.getOrDefault(v.from, 0) + c.count);
				}
			}
			// reactions.addAll(reactions2.stream().map(x->x.name).collect(Collectors.toList()));
		});
		Map<String, Integer> counts = reactions.parallelStream()
				.collect(Collectors.toConcurrentMap(w -> w, w -> 1, Integer::sum));
		Map<User, Integer> counts1 = sortByValue(mmm);
		counts1.keySet().forEach(v -> {
			if (v != null) {
				System.out
						.println(v.name + ":" + counts1.get(v) + ":" + counts1.get(v) / (v.messages().size() + 0.001));
			}
		});
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

	public List<Message> messages() {
		ArrayList<Message> messages = new ArrayList<>();
		this.channels.forEach(v -> messages.addAll(v.messages));
		return messages;
	}

}
