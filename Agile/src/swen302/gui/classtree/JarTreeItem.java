package swen302.gui.classtree;

public class JarTreeItem extends AbstractTreeItem {
	public String label;
	public JarTreeItem(String label) {
		this.label = label;
	}
	@Override
	public String toString() {
		return label;
	}
}
