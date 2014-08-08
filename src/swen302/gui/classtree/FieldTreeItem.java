package swen302.gui.classtree;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.Icon;

import swen302.gui.MainWindow;

public class FieldTreeItem extends AbstractTreeItem {
	public Field field;
	public FieldTreeItem(Field field) {
		this.field = field;

		if(isCheckable())
			checked = MainWindow.DEFAULT_FIELD_SELECTED;
	}

	@Override
	public String toString() {
		return field.getName()+" : "+field.getType().getSimpleName();
	}

	@Override
	public Icon getIcon() {
		int modifiers = field.getModifiers();
		if((modifiers & Modifier.PUBLIC) != 0)
			return Icons.fieldPublicIcon;
		if((modifiers & Modifier.PROTECTED) != 0)
			return Icons.fieldProtectedIcon;
		if((modifiers & Modifier.PRIVATE) != 0)
			return Icons.fieldPrivateIcon;
		return Icons.fieldDefaultIcon;
	}

	@Override
	public boolean isCheckable() {
		int modifiers = field.getModifiers();
		// can't analyze static fields
		if((modifiers & Modifier.STATIC) != 0)
			return false;
		return true;
	}
}
