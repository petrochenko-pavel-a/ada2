package com.onpositive.slack.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.onpositive.slack.data.IListResults;
import com.onpositive.slack.data.IResultItem;

public class ContentList extends AbstractContentPanel{

	public ContentList(Supplier<IListResults> supplier, int pageSize) {
		super(supplier, pageSize);
	}

	@Override
	protected void renderSubList(List<IResultItem> subList, ArrayList<LayoutBlock> content) {
		SectionBlock bl1=new SectionBlock();
		content.add(bl1);
		StringBuilder bld=new StringBuilder();
		for (IResultItem i:subList) {
			bld.append("• "+i.text()+"\n");
		}
		bl1.setText(new MarkdownTextObject(bld.toString(), true));
	}

}
