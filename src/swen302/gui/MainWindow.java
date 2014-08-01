package swen302.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
		            File file = fc.getSelectedFile();
		            //This is where a real application would open the file.

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
			}
		});

		menuBar.add(fileMenu);

		tree = new JTree(new DefaultMutableTreeNode("No file loaded"));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		window.setLayout(new BorderLayout());
		window.add(menuBar, BorderLayout.NORTH);
		window.add(new JButton("Graph placeholder"), BorderLayout.CENTER);
		window.add(new JScrollPane(tree), BorderLayout.WEST);

		tree.setPreferredSize(new Dimension(300, 0));

		window.pack();
		window.setLocationRelativeTo(null);
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
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
				this.methods.add(method.getName() + "()");
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
            	category.add(new DefaultMutableTreeNode(method));
            }
        }
    }

    private class BookInfo {
        public String bookName;
        public URL bookURL;

        public BookInfo(String book, String filename) {
            bookName = book;
        }

        public String toString() {
            return bookName;
        }
    }
}
