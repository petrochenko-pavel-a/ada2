package com.onpositive.slacklogs.model;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.gson.Gson;

public class OdsImporter {

	static class UserProfile{
		String image_oiriginal;
		String image_32;
		String image_192;
	}
	
	static class UserDto{
		String real_name;
		String phone;
		String id;
		String name;
		String color;
		
		String tz;
		String tz_label;
		
		UserProfile profile;
		double tz_offet;
	}
	static class Reaction{
		String name;
		ArrayList<String>users;
		int count;
	}
	static class MessageDto{
		String text;
		String ts;
		String user;
		String thread_ts;
		
		ArrayList<Reaction>reactions;
	}
	
	static ArrayList<UserDto>users;
	static ArrayList<MessageDto>messages;
	
	public static void main(String[] args) {
		Workspace w=new Workspace();
		File users= new File("D:/opendatascience/users.json");
		LinkedHashMap<String, User>usersMap=new LinkedHashMap<String, User>();
		try {
			
			ArrayList<UserDto> fromJson = new Gson().fromJson(new FileReader(users), OdsImporter.class.getDeclaredField("users").getGenericType());
			for (UserDto d:fromJson) {
				User u=new User(d.id);
				usersMap.put(d.id, u);
				u.name=d.name;
				u.real_name=d.real_name;
				u.url=d.profile.image_192.intern();
				w.users.add(u);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		File[] list = new File("D:/opendatascience").listFiles();
		
		int ck=0;
		for (File s:list) {
			if (s.isDirectory()) {
				
				Channel c=readChannel(s,usersMap);
				System.out.println(c.name+":"+c.messages.size());
				w.channels.add(c);
				ck++;
				
			}
		}
		//System.out.println(w);
		try {
			ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream("store2.dat"));
			os.writeObject(w);
			os.close();
			System.out.println("Done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Channel readChannel(File s, LinkedHashMap<String, User> usersMap) {
		Channel ch=new Channel(s.getName());
		for (File f:s.listFiles()) {
			ArrayList<MessageDto> fromJson;
			try {
				FileReader json = new FileReader(f);
				fromJson = new Gson().fromJson(json, OdsImporter.class.getDeclaredField("messages").getGenericType());
				json.close();
				for (MessageDto md:fromJson) {
					Message message=new Message(usersMap.get(md.user),null);
					if (md.reactions!=null) {
						message.reactions=new Message.Reaction[md.reactions.size()];
						int a=0;
						for (Reaction d:md.reactions) {
							Message.Reaction reaction = new Message.Reaction();
							reaction.name=d.name.intern();
							
							User[] mm=new User[d.users.size()];
							int i=0;
							for (String l:d.users) {
								mm[i++]=usersMap.get(l);
							}
							reaction.count=d.count;
							reaction.users=mm;
							message.reactions[a++]=reaction;
						}
						
					}
					message.ts=md.ts.intern();
					if (md.thread_ts!=null) {
						message.thread_ts=md.thread_ts.intern();
					}
					message.channel=ch;
					ch.messages.add(message);					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return ch;
	}
} 