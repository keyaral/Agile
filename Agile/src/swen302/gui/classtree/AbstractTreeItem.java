package swen302.gui.classtree;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * An item in the class tree (left side of the GUI)
 * @author campbealex2
 */
public class AbstractTreeItem extends DefaultMutableTreeNode {
	/**
	 * Returns the icon for this item, or null to not display an icon.
	 */
	public Icon getIcon() {return null;}

	/**
	 * Returns whether this item has a checkbox.
	 */
	public boolean isCheckable() {return false;}

	/**
	 * Whether this item is checked. Meaningless if isCheckable returns false.
	 */
	public boolean checked;
}
