package swen302.gui.classtree;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import swen302.gui.MainWindow;

public class Icons {
	private static Icon getIcon(String name) {
		return new ImageIcon(MainWindow.class.getResource("icons/"+name+".gif"));
	}

	public static final Icon classPublicIcon = getIcon("class_obj");
	public static final Icon classDefaultIcon = getIcon("class_default_obj");
	public static final Icon methodPublicIcon = getIcon("methpub_obj");
	public static final Icon methodPrivateIcon = getIcon("methpri_obj");
	public static final Icon methodDefaultIcon = getIcon("methdef_obj");
	public static final Icon methodProtectedIcon = getIcon("methpro_obj");
	public static final Icon interfacePublicIcon = getIcon("int_obj");
	public static final Icon interfaceDefaultIcon = getIcon("int_default_obj");
	public static final Icon enumPublicIcon = getIcon("enum_obj");
	public static final Icon enumDefaultIcon = getIcon("enum_default_obj");
	public static final Icon packageIcon = getIcon("package_obj");
	public static final Icon fieldPublicIcon = getIcon("field_public_obj");
	public static final Icon fieldPrivateIcon = getIcon("field_private_obj");
	public static final Icon fieldDefaultIcon = getIcon("field_default_obj");
	public static final Icon fieldProtectedIcon = getIcon("field_protected_obj");
}
