package swen302.gui.classtree;

import java.lang.reflect.Field;

import javax.swing.Icon;


public class FieldInGroupTreeItem extends AbstractTreeItem {
	Field field;

	public FieldInGroupTreeItem(Field field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return FieldTreeItem.toString(field);
	}

	@Override
	public Icon getIcon() {
		return FieldTreeItem.getIcon(field);
	}
}
