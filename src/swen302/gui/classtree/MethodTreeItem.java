package swen302.gui.classtree;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.swing.Icon;

import swen302.gui.MainWindow;
import swen302.gui.MethodKey;

public class MethodTreeItem extends AbstractTreeItem {
	public ClassTreeItem clazz;
	public MethodKey method;
	public Method reflectionMethod;

	public MethodTreeItem(ClassTreeItem clazz, MethodKey method, Method reflectionMethod) {
		this.clazz = clazz;
		this.method = method;
		this.reflectionMethod = reflectionMethod;
		if(isCheckable())
			this.checked = MainWindow.DEFAULT_METHOD_SELECTED;
	}

	@Override
	public String toString() {
		return method.name + "(" + method.getReadableArgs() + ")";
	}

	@Override
	public Icon getIcon() {
		int modifiers = reflectionMethod.getModifiers();
		if((modifiers & Modifier.PUBLIC) != 0)
			return Icons.methodPublicIcon;
		if((modifiers & Modifier.PROTECTED) != 0)
			return Icons.methodProtectedIcon;
		if((modifiers & Modifier.PRIVATE) != 0)
			return Icons.methodPrivateIcon;
		return Icons.methodDefaultIcon;
	}

	@Override
	public boolean isCheckable() {
		int modifiers = reflectionMethod.getModifiers();
		// can't analyze abstract, static or native methods
		if((modifiers & (Modifier.ABSTRACT | Modifier.STATIC | Modifier.NATIVE)) != 0)
			return false;
		return true;
	}
}