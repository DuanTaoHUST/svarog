package org.signalml.app.method.ep.view.tags;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("tagStyleGroup")
public class TagStyleGroup {

	private List<String> tagStyleNames = new ArrayList<String>();

	public TagStyleGroup() {
	}

	public TagStyleGroup(String tagName) {
		addTagStyle(tagName);
	}

	public void addTagStyle(String tagStyle) {
		if (!contains(tagStyle)) {
			tagStyleNames.add(tagStyle);
		}
	}

	protected boolean contains(String tagStyle) {
		for (String addedTagStyle: tagStyleNames) {
			if (addedTagStyle.equals(tagStyle))
				return true;
		}
		return false;
	}

	public List<String> getTagStyleNames() {
		return tagStyleNames;
	}

	public int getNumberOfTagStyles() {
		return tagStyleNames.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TagStyleGroup))
			return false;

		TagStyleGroup otherGroup = (TagStyleGroup) obj;
		if (otherGroup.getNumberOfTagStyles() != getNumberOfTagStyles())
			return false;

		for (int i = 0; i < otherGroup.getNumberOfTagStyles(); i++) {
			if (!otherGroup.getTagStyleNames().get(i).equals(getTagStyleNames().get(i)))
				return false;
		}
		return true;
	}

}
