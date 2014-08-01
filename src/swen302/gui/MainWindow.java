package swen302.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import javax.swing.tree.DefaultMutableTreeNode;
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
		            	Enumeration<?> enu = zip.entries();
		    			while (enu.hasMoreElements()) {
		    				ZipEntry zipEntry = (ZipEntry) enu.nextElement();
		    				
						    if(zipEntry.getName().endsWith(".class") && !zipEntry.isDirectory())
						    {
						    	ClassLoader classLoader = MainWindow.class.getClassLoader();
								InputStream is = zip.getInputStream(zipEntry);
								File outputZipFile = new File("output/classes/" + zipEntry);
								
								FileOutputStream fos = new FileOutputStream(outputZipFile);
								
								try
								{
									Class<?> cls = classLoader.loadClass("output/classes/" + zipEntry.getName());
									
									System.out.println(cls.getName());
								}
								catch (Exception classLoadException)
								{
									
								}
						    	
						    	
						    	
						    	
						        StringBuilder className = new StringBuilder();
						        for(String part : zipEntry.getName().split("/"))
						        {
						            if(className.length() != 0) { className.append("."); }
						            className.append(part);
						            if(part.endsWith(".class")) { className.setLength(className.length()-".class".length()); }
						        }
						        classNames.add(className.toString());
						    }
						}
					}
		            catch (IOException exception)
		            {
						exception.printStackTrace();
					}
		            
		            DefaultMutableTreeNode top = new DefaultMutableTreeNode(file.getName());
		    	    createNodes(top, classNames);
		    	        
		    		tree = new JTree(top);
		            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		            
		            JScrollPane treeView = new JScrollPane(tree);
		            
		            window.add(treeView, BorderLayout.WEST);
		        }
			}
		});
		
		menuBar.add(fileMenu);

		window.setLayout(new BorderLayout());
		window.add(menuBar, BorderLayout.NORTH);
		window.add(new JButton("Graph"), BorderLayout.CENTER);

		
		
		
		window.pack();
		window.setLocationRelativeTo(null);
		
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
