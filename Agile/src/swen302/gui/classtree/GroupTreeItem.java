package swen302.gui.classtree;

import javax.swing.Icon;

public class GroupTreeItem extends AbstractTreeItem {
	Class<?> ownerClass;

	public GroupTreeItem(Class<?> ownerClass) {
		this.ownerClass = ownerClass;
	}

	public Class<?> getOwnerClass() {
		return ownerClass;
	}

	@Override
	public String toString() {
		return "Field group";
	}

	@Override
	public Icon getIcon() {
		return Icons.fieldGroupIcon;
	}
}