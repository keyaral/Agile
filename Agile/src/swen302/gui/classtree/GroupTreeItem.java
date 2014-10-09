package swen302.gui.classtree;

import javax.swing.Icon;

public class GroupTreeItem extends AbstractTreeItem {
	String name;

	public GroupTreeItem(String name) {
		this.name= name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return Icons.classPublicIcon;
	}
}