package swen302.gui.classtree;

import java.lang.reflect.Modifier;

import javax.swing.Icon;

import swen302.gui.MainWindow;

public class ParameterTreeItem extends AbstractTreeItem {
	public MethodTreeItem parent;
	public int argNum;
	public String argName;
	public String argType;

	public ParameterTreeItem(MethodTreeItem parent, int argNum) {
		this.parent = parent;
		if(isCheckable())
			this.checked = MainWindow.DEFAULT_METHOD_SELECTED;
		this.argNum = argNum;
		this.argType = String.valueOf(parent.reflectionMethod.getGenericParameterTypes()[argNum]);
		this.argName = "par"+argNum;
	}

	@Override
	public String toString() {
		return argName+" ("+argType+")";
	}

	int x;
	@Override
	public Icon getIcon() {
		return Icons.fieldPublicIcon;
	}

	@Override
	public boolean isCheckable() {
		int modifiers = parent.reflectionMethod.getModifiers();
		// can't analyze abstract, static or native methods
		if((modifiers & (Modifier.ABSTRACT | Modifier.STATIC | Modifier.NATIVE)) != 0)
			return false;
		return true;
	}
}