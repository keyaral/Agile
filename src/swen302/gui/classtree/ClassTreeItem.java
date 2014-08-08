package swen302.gui.classtree;

import java.lang.reflect.Modifier;

import javax.swing.Icon;

public class ClassTreeItem extends AbstractTreeItem {
	Class<?> clazz;
	String shortName;
	public ClassTreeItem(Class<?> clazz) {
		this.clazz = clazz;
		this.shortName = clazz.getName();
		if(shortName.contains("."))
			shortName = shortName.substring(shortName.lastIndexOf('.')+1);
	}
	@Override
	public String toString() {
		return shortName;
	}

	@Override
	public Icon getIcon() {
		boolean isPublic = (clazz.getModifiers() & Modifier.PUBLIC) != 0;
		if(clazz.isInterface())
			return isPublic ? Icons.interfacePublicIcon : Icons.interfaceDefaultIcon;
		else if(clazz.isEnum())
			return isPublic ? Icons.enumPublicIcon : Icons.enumDefaultIcon;
		else
			return isPublic ? Icons.classPublicIcon : Icons.classDefaultIcon;
	}
}