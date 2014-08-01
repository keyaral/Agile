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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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

		            ArrayList<String> classNames = new ArrayList<String>();

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

						    	System.out.println(cls.getName());
						    }
						}

		    			zipClassLoader.close();
					}
		            catch (IOException | ClassNotFoundException exception)
		            {
						exception.printStackTrace();
					}

		            DefaultMutableTreeNode top = new DefaultMutableTreeNode(file.getName());
		    	    createNodes(top, classNames);

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

    private void createNodes(DefaultMutableTreeNode top, ArrayList<String> classNames) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;

        for (String name : classNames)
        {
        	category = new DefaultMutableTreeNode(name);
            top.add(category);
            category.add(new DefaultMutableTreeNode("FieldName"));
            category.add(new DefaultMutableTreeNode("MethodName"));
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
