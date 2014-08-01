package swen302.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import swen302.analysis.JarLoader;
import swen302.analysis.JarLoader.JarData;
import swen302.graph.Graph;
import swen302.automaton.Main;
import swen302.automaton.VisualizationAlgorithm;
import swen302.graph.GraphSaver;
import swen302.tracer.Trace;
import swen302.tracer.TraceMethodFilter;
import swen302.tracer.Tracer;

public class MainWindow {

	private JFrame window;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem fileLoadJAR, fileLoadAdvanced, fileLoadConfig, fileSaveConfig, fileExit;
	private JTree tree;
	private ImagePane graphPane;
	private final JFileChooser fc = new JFileChooser();

	private JarData jarData;

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
		            jarData = JarLoader.loadJarFile(fc.getSelectedFile());
		            
		            DefaultMutableTreeNode top = new DefaultMutableTreeNode(fc.getSelectedFile().getName());
					((DefaultTreeModel)tree.getModel()).setRoot(top);
		        }
			}
		});

		menuBar.add(fileMenu);

		tree = new JTree(new DefaultMutableTreeNode("No file loaded"));
		tree.setCellRenderer(new ClassTreeCellRenderer());
		tree.setCellEditor(new ClassTreeCellEditor());
		tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        graphPane = new ImagePane();

		window.setLayout(new BorderLayout());
		window.add(menuBar, BorderLayout.NORTH);
		window.add(graphPane, BorderLayout.CENTER);
		window.add(new JScrollPane(tree), BorderLayout.WEST);

		window.pack();
		window.setLocationRelativeTo(null);
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		// for testing
		File testfile = new File("testprogs/CompassRotating.jar");
		if(testfile.exists())
		{
			jarData = JarLoader.loadJarFile(testfile);
			
			DefaultMutableTreeNode top = new DefaultMutableTreeNode(testfile.getName());
			((DefaultTreeModel)tree.getModel()).setRoot(top);
		}
	}

	public void setVisible(boolean visible) {
		window.setVisible(visible);
	}

	private void doTraceAndAnalysis() {
		TraceMethodFilter filter = new TraceMethodFilter() {
			private Set<String> selectedMethods = new HashSet<String>();

			{
				for(MethodTreeItem i : allMethodTreeItems)
					if(i.checked)
						selectedMethods.add(i.className+"."+i.name);
			}

			@Override
			public boolean isMethodTraced(com.sun.jdi.Method m) {
				boolean result = selectedMethods.contains(m.declaringType().name()+"."+m.name());
				if(result) System.out.println(m);
				return result;
			}

		};

		try {
			String path = jarData.file.getAbsolutePath();
			String mainClass = jarData.manifest.getMainAttributes().getValue(Name.MAIN_CLASS);
			
			Trace trace = Tracer.Trace("-cp \"" + path + "\"", mainClass, filter);
			
			VisualizationAlgorithm algo = new Main();
			Graph graph = algo.generateGraph(trace);

			File pngfile = new File("tempAnalysis.png");
			GraphSaver.save(graph, pngfile);
			BufferedImage image = ImageIO.read(pngfile);

			graphPane.setImage(image);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<MethodTreeItem> allMethodTreeItems = new ArrayList<MethodTreeItem>();

    private void createNodes(DefaultMutableTreeNode top, ArrayList<Class<?>> classData) {
        DefaultMutableTreeNode category = null;

        allMethodTreeItems.clear();

        for (Class<?> data : classData)
        {
        	category = new DefaultMutableTreeNode(data.getName());
            top.add(category);

            for (Field field : data.getDeclaredFields()){
            	category.add(new DefaultMutableTreeNode(field.getName()));
            }

            for (Method method : data.getDeclaredMethods()){

            	StringBuilder argsString = new StringBuilder();
				argsString.append('(');
				Class<?>[] argTypes = method.getParameterTypes();
				for(Class<?> argType : argTypes) {
					argsString.append(argType.getSimpleName());
					argsString.append(", ");
				}
				if(argTypes.length != 0)
					argsString.setLength(argsString.length() - 2);
				argsString.append(')');


            	MethodTreeItem treeItem = new MethodTreeItem(data.getName(), method.getName(), argsString.toString());
            	allMethodTreeItems.add(treeItem);
            	category.add(new DefaultMutableTreeNode(treeItem));
            }
        }
    }





    private class MethodTreeItem {
    	String name;
    	String className;
    	String args;
    	boolean checked = true;
    	public MethodTreeItem(String className, String name, String argsString) {
    		this.className = className;
    		this.name = name;
    		this.args = argsString;
    	}
    	@Override
    	public String toString() {
    		return name+args;
    	}
    }



	private class ClassTreeCellRenderer implements TreeCellRenderer {

		private JLabel label = new JLabel();
		private JCheckBox checkBox = new JCheckBox();

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			value = ((DefaultMutableTreeNode)value).getUserObject();
			try {
				if(value instanceof MethodTreeItem) {
					checkBox.setSelected(((MethodTreeItem)value).checked);
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
		private static final long serialVersionUID = 1L;

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
						((MethodTreeItem)value).checked = ((JCheckBox)rv).isSelected();
						doTraceAndAnalysis();
					}
				});
			}
			return rv;
		}

	}
}
