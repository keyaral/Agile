package swen302.gui.classtree;

import javax.swing.Icon;

public class GroupTreeItem extends AbstractTreeItem {
	String name;
	Class<?> ownerClass;

	public GroupTreeItem(String name, Class<?> ownerClass) {
		this.name= name;
		this.ownerClass = ownerClass;
	}

	public Class<?> getOwnerClass() {
		return ownerClass;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return Icons.fieldGroupIcon;
	}
}