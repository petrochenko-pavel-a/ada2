package com.onpositive.slack.client;

import java.util.ArrayList;

import com.github.seratch.jslack.api.model.block.ImageBlock;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;

public class ImageMessage extends UpdatableMessage{

	protected String title;
	private String url;
	
	public ImageMessage(String title,String url) {
		super();
		this.title = title;
		this.url=url;
	}

	@Override
	protected void fillContent(ArrayList<LayoutBlock> content) {
		// TODO Auto-generated method stub
		SectionBlock e = new SectionBlock();
		e.setText(new PlainTextObject(this.title,false));
		content.add(e);
		ImageBlock bl=new ImageBlock();
		bl.setImageUrl(url);
		bl.setAltText(title);
		content.add(bl);
	}

	@Override
	protected void fillActions(ArrayList<Action> actions) {
		
	}

}
