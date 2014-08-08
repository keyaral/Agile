package swen302.gui.classtree;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import swen302.gui.MainWindow;

public class Icons {
	private static Icon getIcon(String name) {
		return new ImageIcon(MainWindow.class.getResource("icons/"+name+".gif"));
	}
	public static Icon classPublicIcon = getIcon("class_obj");
	public static Icon classDefaultIcon = getIcon("class_default_obj");
	public static Icon methodPublicIcon = getIcon("methpub_obj");
	public static Icon methodPrivateIcon = getIcon("methpri_obj");
	public static Icon methodDefaultIcon = getIcon("methdef_obj");
	public static Icon methodProtectedIcon = getIcon("methpro_obj");
	public static Icon interfacePublicIcon = getIcon("int_obj");
	public static Icon interfaceDefaultIcon = getIcon("int_default_obj");
	public static Icon enumPublicIcon = getIcon("enum_obj");
	public static Icon enumDefaultIcon = getIcon("enum_default_obj");
	public static Icon packageIcon = getIcon("package_obj");
}
