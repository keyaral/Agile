package swen302.gui.classtree;

import javax.swing.Icon;

public class PackageTreeItem extends AbstractTreeItem {
	String packageName;
	public PackageTreeItem(String packageName) {
		this.packageName = packageName;
	}
	@Override
	public String toString() {
		return packageName;
	}
	@Override
	public Icon getIcon() {
		return Icons.packageIcon;
	}
}