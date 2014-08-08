package swen302.gui.classtree;

import java.lang.reflect.Field;

public class FieldTreeItem extends AbstractTreeItem {
	public Field field;
	public FieldTreeItem(Field field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return field.getName();
	}
}
