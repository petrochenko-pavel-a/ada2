package com.onpositive.slack.data;

import java.util.ArrayList;
import java.util.List;

public class ResultList implements IListResults{

	protected String title;
	
	public ResultList(String title, List<Object> results) {
		super();
		this.title = title;
		this.results = results;
	}

	protected List<Object>results;
	
	@Override
	public String title() {
		return title;
	}

	@Override
	public List<IResultItem> items() {
		ArrayList<IResultItem>r=new ArrayList<>();
		results.forEach(v->{
			r.add(new IResultItem() {
				
				@Override
				public String text() {
					return v.toString();
				}
				
				@Override
				public String imageUrl() {
					return null;
				}
			});
		});
		return r;
	}

	
}
