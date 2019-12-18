package com.onpositive.slack.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.OptionObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.OverflowMenuElement;
import com.onpositive.slack.data.IListResults;
import com.onpositive.slack.data.IResultItem;

public abstract class AbstractContentPanel extends UpdatableMessage {

	protected Supplier<IListResults> supplier;

	protected int size;

	protected int pageSize;

	protected int offset;

	public AbstractContentPanel(Supplier<IListResults> supplier, int pageSize) {
		super();
		this.pageSize = pageSize;
		this.supplier = supplier;
	}

	@Override
	protected void fillContent(ArrayList<LayoutBlock> content) {
		IListResults iListResults = supplier.get();
		List<IResultItem> items = iListResults.items();
		this.size = items.size();
		SectionBlock sectionBlock = new SectionBlock();
		sectionBlock.setText(new PlainTextObject(iListResults.title(), false));
		content.add(sectionBlock);
		List<String> loptions = options();
		if (!loptions.isEmpty()) {
			
			OverflowMenuElement accessory = new OverflowMenuElement();
			ArrayList<OptionObject> options = new ArrayList<>();
			accessory.setOptions(options);
			for (String s:loptions) {
				options.add(new OptionObject(new PlainTextObject(s, false), s));
				if (currentOption==null) {
					currentOption=s;
				}
			}
			accessory.setActionId(uid);
			sectionBlock.setAccessory(accessory);
		}
		renderSubList(items.subList(offset, Math.min(this.size, offset + pageSize)), content);
	}

	protected List<String> options() {
		return Collections.emptyList();
	}
	protected String currentOption;
	
	@Override
	protected boolean menuOption(String value) {
		if (!currentOption.equals(value)) {
			currentOption=value;
			return true;
		}
		return false;
	}
	

	protected abstract void renderSubList(List<IResultItem> subList, ArrayList<LayoutBlock> content);

	@Override
	protected void fillActions(ArrayList<Action> actions) {
		if (this.offset > 0) {
			actions.add(new Action("Prev") {

				@Override
				public boolean run() {
					offset = Math.max(offset - pageSize, 0);
					return true;
				}
			});
		}
		if (this.size - this.offset > pageSize) {
			actions.add(new Action("Next") {

				@Override
				public boolean run() {
					offset = Math.min(size - pageSize, offset + pageSize);
					return true;
				}
			});
		}
	}

}