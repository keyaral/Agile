package swen302.gui.classtree;

import java.lang.reflect.Field;

import javax.swing.Icon;

import swen302.tracer.FieldKey;


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

	public FieldKey getFieldKey() {
		return new FieldKey(field);
	}
}
