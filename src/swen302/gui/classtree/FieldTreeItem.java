package swen302.gui.classtree;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.Icon;

public class FieldTreeItem extends AbstractTreeItem {
	public Field field;
	public FieldTreeItem(Field field) {
		this.field = field;
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
