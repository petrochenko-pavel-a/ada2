package com.onpositive.slacklogs.model;

import java.time.ZoneOffset;

public class Tes1 {

	public static void main(String[] args) {
		Workspace.getInstance().messages().parallelStream().filter(x->x.text.contains("GAN")&&x.channel.name.contains("jobs")&&x.getDate().atZone(ZoneOffset.UTC).toLocalDate().getYear()==2019).forEach(c->{
			System.out.println(c.from+":"+c.getDate());
		});
	}

}
