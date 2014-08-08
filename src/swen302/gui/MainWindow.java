package swen302.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes.Name;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import swen302.analysis.JarLoader;
import swen302.analysis.JarLoader.JarData;
import swen302.automaton.KTailsAlgorithm;
import swen302.automaton.VisualizationAlgorithm;
import swen302.graph.Graph;
import swen302.graph.GraphSaver;
import swen302.gui.classtree.AbstractTreeItem;
import swen302.gui.classtree.ClassTreeItem;
import swen302.gui.classtree.FieldTreeItem;
import swen302.gui.classtree.JarTreeItem;
import swen302.gui.classtree.MethodTreeItem;
import swen302.gui.classtree.PackageTreeItem;
import swen302.tracer.Trace;
import swen302.tracer.TraceMethodFilter;
import swen302.tracer.Tracer;

public class MainWindow {

	/** Whether methods are selected by default */
	public static final boolean DEFAULT_METHOD_SELECTED = true;

	private JFrame window;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem fileLoadJAR, fileLoadAdvanced, fileLoadConfig, fileSaveConfig, fileExit;
	private JTree tree;
	private ImagePane graphPane;

	private JarData jarData;

	private File lastJarDirectory = new File(".");
	private File lastConfigDirectory = new File(".");

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

		fileLoadAdvanced.setEnabled(false); // not implemented

		fileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		fileLoadJAR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lastJarDirectory);
				fc.setFileFilter(new FileNameExtensionFilter("JAR files", "zip", "jar"));
				int returnVal = fc.showOpenDialog(window);

				lastJarDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					jarData = JarLoader.loadJarFile(fc.getSelectedFile());


		            DefaultMutableTreeNode top = new DefaultMutableTreeNode(new JarTreeItem(fc.getSelectedFile().getName()));

					((DefaultTreeModel)tree.getModel()).setRoot(top);

					createNodes(top, jarData.data);
					doTraceAndAnalysis();
				}
			}
		});

		fileSaveConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lastConfigDirectory);
				fc.setFileFilter(new FileNameExtensionFilter("Configuration files", "cfg"));
				int returnVal = fc.showSaveDialog(window);

				File file = fc.getSelectedFile();
				if(!fc.getFileFilter().accept(file))
					file = new File(file.toString()+".cfg");

				lastConfigDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					TracerConfiguration conf = new TracerConfiguration();
					saveToConfiguration(conf);
					try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
						out.writeObject(conf);
					} catch(IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		fileLoadConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lastConfigDirectory);
				fc.setFileFilter(new FileNameExtensionFilter("Configuration files", "cfg"));
				int returnVal = fc.showOpenDialog(window);

				File file = fc.getSelectedFile();

				lastConfigDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
						TracerConfiguration conf = (TracerConfiguration)in.readObject();
						loadFromConfiguration(conf);

					} catch(IOException | ClassNotFoundException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		menuBar.add(fileMenu);

		tree = new JTree(new DefaultMutableTreeNode(new JarTreeItem("No file loaded")));
		tree.setCellRenderer(new ClassTreeCellRenderer());
		tree.setCellEditor(new ClassTreeCellEditor());
		tree.setEditable(true);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setPreferredSize(new Dimension(300, 1));


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
			loadJarFile(testfile);

			doTraceAndAnalysis();
		}

	}

	private void loadJarFile(File testfile) {
		jarData = JarLoader.loadJarFile(testfile);

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new JarTreeItem(testfile.getName()));
		((DefaultTreeModel)tree.getModel()).setRoot(top);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				tree.expandPath(new TreePath(new Object[] {tree.getModel().getRoot()}));
			}
		});

		createNodes(top, jarData.data);
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
						selectedMethods.add(i.method.className+"."+i.method.name);
			}

			@Override
			public boolean isMethodTraced(com.sun.jdi.Method m) {
				return selectedMethods.contains(m.declaringType().name()+"."+m.name());
			}

		};

		try {
			String path = jarData.file.getAbsolutePath();
			String mainClass = jarData.manifest.getMainAttributes().getValue(Name.MAIN_CLASS);

			int traceCount = 1;
			Trace[] traces = new Trace[traceCount];
			for(int i=0; i<traceCount; i++){
				traces[i] = Tracer.Trace("-cp \"" + path + "\"", mainClass, filter);
			}

			//Trace trace = Tracer.Trace("-cp \"" + path + "\"", mainClass, filter);

			//Trace.writeFile(trace, "debugLastTrace.txt");

			VisualizationAlgorithm algo = new KTailsAlgorithm();//new FieldBasedAlgorithm(); //TODO set which algorithm
			Graph graph = algo.generateGraph(traces);

			File pngfile = new File("tempAnalysis.png");
			GraphSaver.save(graph, pngfile);
			BufferedImage image = ImageIO.read(pngfile);

			graphPane.setImage(image);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void saveToConfiguration(TracerConfiguration conf) {
		conf.jarFile = jarData.file;
		for(MethodTreeItem mti : allMethodTreeItems)
			conf.selectedMethods.put(mti.method, mti.checked);
	}

	public void loadFromConfiguration(TracerConfiguration conf) {
		loadJarFile(conf.jarFile);

		for(MethodTreeItem mti : allMethodTreeItems) {
			Boolean saved = conf.selectedMethods.get(mti.method);
			mti.checked = (saved != null ? saved : DEFAULT_METHOD_SELECTED);
		}

		doTraceAndAnalysis();
	}


	private List<MethodTreeItem> allMethodTreeItems = new ArrayList<MethodTreeItem>();


    private void createNodes(DefaultMutableTreeNode top, ArrayList<Class<?>> classData) {
        allMethodTreeItems.clear();

        Map<String, DefaultMutableTreeNode> packages = new HashMap<>();

        Map<Class<?>, DefaultMutableTreeNode> classNodes = new HashMap<Class<?>, DefaultMutableTreeNode>();

        // For each class, generate the subtree corresponding to that class
        for (Class<?> data : classData)
        	classNodes.put(data, createClassNodes(data));

        // Then insert the subtrees at the right locations (under packages or other classes)
        for(Map.Entry<Class<?>, DefaultMutableTreeNode> entry : classNodes.entrySet()) {
        	Class<?> clazz = entry.getKey();

        	DefaultMutableTreeNode parentNode;

        	Class<?> enclosingClass = clazz.getEnclosingClass();

        	if(enclosingClass == null) {

        		// Top-level class; insert under a package
            	String packageName = getPackageName(clazz);

            	DefaultMutableTreeNode packageNode = packages.get(packageName);
            	if(packageNode == null) {
            		// If no node exists for this package, create one
            		packageNode = new DefaultMutableTreeNode(new PackageTreeItem(packageName));
            		packages.put(packageName, packageNode);
            		top.add(packageNode);
            	}

            	parentNode = packageNode;

        	} else {
        		// Nested class; insert under the enclosing class
        		parentNode = classNodes.get(enclosingClass);
        	}

        	parentNode.add(entry.getValue());
        }


    }


    /** Returns the package name, as shown in the class tree - e.g. "swen302.testprograms" or "(default package)" */
    private String getPackageName(Class<?> clazz) {
    	String className = clazz.getName();
    	if(className.contains("."))
    		return className.substring(0, className.lastIndexOf('.'));
    	else
    		return "(default package)";
    }

    /** Creates a subtree of the class tree from one class. */
    private DefaultMutableTreeNode createClassNodes(Class<?> data) {
    	ClassTreeItem classItem = new ClassTreeItem(data);

    	DefaultMutableTreeNode category = new DefaultMutableTreeNode(classItem);

        for (Field field : data.getDeclaredFields()){
        	FieldTreeItem fti = new FieldTreeItem(field);
        	if(field.isSynthetic() && !fti.isCheckable())
        		continue;
        	category.add(new DefaultMutableTreeNode(fti));
        }

        for (Method method : data.getDeclaredMethods()) {
        	MethodTreeItem treeItem = new MethodTreeItem(classItem, new MethodKey(method), method);
        	if(method.isSynthetic() && !treeItem.isCheckable())
        		continue;
        	allMethodTreeItems.add(treeItem);
        	category.add(new DefaultMutableTreeNode(treeItem));
        }
        return category;
    }




    private class CheckBoxIconPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		JLabel label = new JLabel();
		JCheckBox checkBox = new JCheckBox();

		{
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.add(label);
			this.add(checkBox);
			this.setOpaque(false);
		}
	}




    private class ClassTreeCellRenderer implements TreeCellRenderer {

		private JLabel label = new JLabel();

		private CheckBoxIconPanel checkBoxPanel = new CheckBoxIconPanel();

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			value = ((DefaultMutableTreeNode)value).getUserObject();
			try {
				AbstractTreeItem item = (AbstractTreeItem)value;
				if(item.isCheckable()) {
					checkBoxPanel.label.setIcon(item.getIcon());
					checkBoxPanel.checkBox.setSelected(item.checked);
					checkBoxPanel.checkBox.setText(value.toString());
					return checkBoxPanel;
				} else {
					label.setIcon(item.getIcon());
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
			if(rv instanceof CheckBoxIconPanel) {
				((CheckBoxIconPanel)rv).checkBox.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if(stopCellEditing())
							fireEditingStopped();
						((AbstractTreeItem)value).checked = ((CheckBoxIconPanel)rv).checkBox.isSelected();
						doTraceAndAnalysis();
					}
				});
			}
			return rv;
		}

	}
}
