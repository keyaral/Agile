package swen302.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.EventObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class MainWindow {

	private JFrame window;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem fileLoadJAR, fileLoadAdvanced, fileLoadConfig, fileSaveConfig, fileExit;
	private JTree tree;
	private final JFileChooser fc = new JFileChooser();

	public MainWindow() {
		window = new JFrame("UltimaTracer 9000");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		fileLoadJAR = fileMenu.add("Load JAR...");
		fileLoadAdvanced = fileMenu.add("Load Advanced...");
		fileMenu.addSeparator();
		fileLoadConfig = fileMenu.add("Load Config...");
		fileSaveConfig = fileMenu.add("Save Config...");
		fileMenu.addSeparator();
		fileExit = fileMenu.add("Exit");

		fileLoadJAR.setEnabled(true);
		fileLoadAdvanced.setEnabled(true);
		fileLoadConfig.setEnabled(false);
		fileSaveConfig.setEnabled(false);

		fileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		fc.setFileFilter(new FileNameExtensionFilter("JAR files", "zip", "jar"));

		fileLoadJAR.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int returnVal = fc.showOpenDialog(window);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            loadJarFile(fc.getSelectedFile());
		        }
			}
		});

		menuBar.add(fileMenu);

		tree = new JTree(new DefaultMutableTreeNode("No file loaded"));
		tree.setCellRenderer(new ClassTreeCellRenderer());
		tree.setCellEditor(new ClassTreeCellEditor());
		tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		window.setLayout(new BorderLayout());
		window.add(menuBar, BorderLayout.NORTH);
		window.add(new JButton("Graph placeholder"), BorderLayout.CENTER);
		window.add(new JScrollPane(tree), BorderLayout.WEST);

		window.pack();
		window.setLocationRelativeTo(null);
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		// for testing
		File testfile = new File("testprogs/CompassRotating.jar");
		if(testfile.exists())
			loadJarFile(testfile);
	}

	public void setVisible(boolean visible) {
		window.setVisible(visible);
	}

	private class TreeNodeData {

		private String name;
		private ArrayList<String> fields;
		private ArrayList<String> methods;

		public String getName() { return name; }
		public void setName(String name) { this.name = name; }

		public ArrayList<String> getFieldNames() { return fields; }
		public void setFields(ArrayList<String> fields) { this.fields = fields; }

		public ArrayList<String> getMethods() { return methods; }
		public void setMethods(ArrayList<String> methods) { this.methods = methods; }

		public TreeNodeData(String name, Field[] fields, Method[] methods){
			this.name = name;
			this.fields = new ArrayList<String>();
			this.methods = new ArrayList<String>();

			List<Field> listFields = Arrays.asList(fields);
			List<Method> listMethods = Arrays.asList(methods);

			for (Field field : listFields)
			{
				this.fields.add(field.getName());
			}

			for (Method method : listMethods)
			{
				StringBuilder dispString = new StringBuilder();
				dispString.append(method.getName());
				dispString.append('(');
				Class<?>[] argTypes = method.getParameterTypes();
				for(Class<?> argType : argTypes) {
					dispString.append(argType.getSimpleName());
					dispString.append(", ");
				}
				if(argTypes.length != 0)
					dispString.setLength(dispString.length() - 2);
				dispString.append(')');
				this.methods.add(dispString.toString());
			}
		}


	}

    private void createNodes(DefaultMutableTreeNode top, ArrayList<TreeNodeData> classData) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;

        for (TreeNodeData data : classData)
        {
        	category = new DefaultMutableTreeNode(data.getName());
            top.add(category);

            for (String field : data.getFieldNames()){
            	category.add(new DefaultMutableTreeNode(field));
            }

            for (String method : data.getMethods()){
            	category.add(new DefaultMutableTreeNode(new ClassTreeItem(method)));
            }
        }
    }

    private void loadJarFile(File file) {
		ArrayList<TreeNodeData> classData = new ArrayList<TreeNodeData>();

		ZipFile zip = null;
		try
		{
			zip = new ZipFile(file.getAbsoluteFile());
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}

		try
		{
			URLClassLoader zipClassLoader = new URLClassLoader(new URL[] {file.toURI().toURL()});

		    Enumeration<?> enu = zip.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

			    if(zipEntry.getName().endsWith(".class") && !zipEntry.isDirectory())
			    {
			    	String className = zipEntry.getName().replace("/", ".");
			    	className = className.substring(0, className.length() - 6);

			    	Class<?> cls = zipClassLoader.loadClass(className);

			    	classData.add(new TreeNodeData(cls.getName(), cls.getDeclaredFields(), cls.getDeclaredMethods()));
			    }
			}

			zipClassLoader.close();
		}
		catch (IOException | ClassNotFoundException exception)
		{
			exception.printStackTrace();
		}

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(file.getName());
		createNodes(top, classData);

		((DefaultTreeModel)tree.getModel()).setRoot(top);
	}



    private class ClassTreeItem {
    	String name;
    	boolean checked;
    	public ClassTreeItem(String name) {
    		this.name = name;
    		this.checked = false;
    	}
    	@Override
    	public String toString() {
    		return name;
    	}
    }



	private class ClassTreeCellRenderer implements TreeCellRenderer {

		private JLabel label = new JLabel();
		private JCheckBox checkBox = new JCheckBox();

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			value = ((DefaultMutableTreeNode)value).getUserObject();
			try {
				if(value instanceof ClassTreeItem) {
					checkBox.setSelected(((ClassTreeItem)value).checked);
					checkBox.setText(value.toString());
					return checkBox;
				} else {
					label.setText(value.toString());
					return label;
				}
			} catch(StackOverflowError e) {
				System.exit(1);
				return null;
			}
		}
    }

	private class ClassTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

		Object value;

		@Override
		public Object getCellEditorValue() {
			return value;
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree, final Object value_, boolean selected, boolean expanded, boolean leaf, int row) {
			final Component rv = new ClassTreeCellRenderer().getTreeCellRendererComponent(tree, value_, selected, expanded, leaf, row, true);
			this.value = ((DefaultMutableTreeNode)value_).getUserObject();
			if(rv instanceof JCheckBox) {
				((JCheckBox)rv).addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if(stopCellEditing())
							fireEditingStopped();
						((ClassTreeItem)value).checked = ((JCheckBox)rv).isSelected();
					}
				});
			}
			return rv;
		}

	}
}
