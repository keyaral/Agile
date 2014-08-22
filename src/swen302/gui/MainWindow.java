package swen302.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes.Name;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import swen302.analysis.JarLoader;
import swen302.analysis.JarLoader.JarData;
import swen302.automaton.IncrementalVisualizationAlgorithm;
import swen302.automaton.KTailsAlgorithm;
import swen302.automaton.VisualizationAlgorithm;
import swen302.execution.ExecutionData;
import swen302.graph.Graph;
import swen302.graph.GraphSaver;
import swen302.gui.classtree.AbstractTreeItem;
import swen302.gui.classtree.ClassTreeItem;
import swen302.gui.classtree.FieldTreeItem;
import swen302.gui.classtree.JarTreeItem;
import swen302.gui.classtree.MethodTreeItem;
import swen302.gui.classtree.PackageTreeItem;
import swen302.tracer.FutureTraceConsumer;
import swen302.tracer.RealtimeTraceConsumer;
import swen302.tracer.Trace;
import swen302.tracer.TraceFieldFilter;
import swen302.tracer.TraceMethodFilter;
import swen302.tracer.Tracer;

public class MainWindow {

	/** Whether methods are selected by default */
	public static final boolean DEFAULT_METHOD_SELECTED = false;
	/** Whether fields are selected by default */
	public static final boolean DEFAULT_FIELD_SELECTED = true;

	/** Whether the tracing and analysis will be run whenever the filter is changed. */
	public static final boolean AUTO_RUN = false;

	private JFrame window;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem fileLoadJAR, fileLoadTrace, fileLoadAdvanced, fileEditExecutions, fileLoadConfig, fileSaveConfig, fileExit, fileChangeK;
	private JMenu displayMenu;
	private JCheckBoxMenuItem displayID, displayState, displayClass, displayMethod, displayParams;
	private JTree tree;
	private JPanel treePanel;
	private JPanel configPanel;
	private ImagePane graphPane;
	private JPopupMenu treePopup;
	private JMenuItem popupSelect, popupDeselect;

	private JComboBox<AlgorithmComboBoxWrapper> cmbAlgorithm;
	private JCheckBox chkContinuousUpdating;
	private JCheckBox saveTracesCheckbox;
	private JButton runButton;

	private JarData jarData;

	private File lastJarDirectory = new File(".");
	private File lastConfigDirectory = new File(".");
	private File lastTraceDirectory = new File(".");

	private TreePath selectedPath;

	private List<ExecutionData> executions = new ArrayList<>(Arrays.asList(new ExecutionData()));

	/**
	 * Instances of this are used in the combo box's item list, as they implement toString.
	 * @author campbealex2
	 */
	private static class AlgorithmComboBoxWrapper {
		Class<? extends VisualizationAlgorithm> algClass;
		String name;
		AlgorithmComboBoxWrapper(Class<? extends VisualizationAlgorithm> algClass) {
			this.algClass = algClass;
			try {
				name = algClass.newInstance().toString();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

		VisualizationAlgorithm createInstance() {
			try {
				return algClass.newInstance();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String toString() {
			return name;
		}
	}


	public MainWindow() {
		window = new JFrame("SWEN302 Program Tracer");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		fileLoadJAR = fileMenu.add("Load JAR...");
		fileLoadTrace = fileMenu.add("Load Trace...");
		fileLoadAdvanced = fileMenu.add("Load Advanced...");
		fileMenu.addSeparator();
		fileEditExecutions = fileMenu.add("Edit Executions...");
		fileMenu.addSeparator();
		fileLoadConfig = fileMenu.add("Load Config...");
		fileSaveConfig = fileMenu.add("Save Config...");
		fileMenu.addSeparator();
		fileChangeK = fileMenu.add("Change K Value...");
		fileMenu.addSeparator();
		fileExit = fileMenu.add("Exit");

		displayMenu = new JMenu("Display");

		displayID = new JCheckBoxMenuItem("ID",true);
		displayMenu.add(displayID);
		displayState = new JCheckBoxMenuItem("State",true);
		displayMenu.add(displayState);
		displayClass = new JCheckBoxMenuItem("Class",true);
		displayMenu.add(displayClass);
		displayMethod = new JCheckBoxMenuItem("Method",true);
		displayMenu.add(displayMethod);
		displayParams = new JCheckBoxMenuItem("Parameters",true);
		displayMenu.add(displayParams);

		treePopup = new JPopupMenu();
		popupSelect = treePopup.add("Select All");
		popupDeselect = treePopup.add("Deselect All");

		popupSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkAllBoxes(selectedPath, true);
			}
		});

		popupDeselect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkAllBoxes(selectedPath, false);
			}
		});

		displayID.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GraphSaver.displayID = displayID.isSelected();
			}
		});

		displayState.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GraphSaver.displayState = displayState.isSelected();
			}
		});

		displayClass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GraphSaver.displayClass = displayClass.isSelected();
			}
		});

		displayMethod.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GraphSaver.displayMethod = displayMethod.isSelected();
			}
		});

		displayParams.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GraphSaver.displayParams = displayParams.isSelected();
			}
		});

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


					executions.clear();
					executions.add(new ExecutionData());


					if(AUTO_RUN)
						doTraceAndAnalysis();
					else
						graphPane.setImage(null);
				}
			}
		});

		fileLoadTrace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lastTraceDirectory);
				fc.setFileFilter(new FileNameExtensionFilter("Trace files", "trace"));
				int returnVal = fc.showOpenDialog(window);

				lastTraceDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						TraceFile tf = new TraceFile();
						tf.read(fc.getSelectedFile());
						loadFromConfiguration(tf.config);
						processTraces(tf.traces);
					} catch(IOException | InterruptedException exc) {
						throw new RuntimeException(exc); // TODO handle error
					}
				}
			}
		});

		fileSaveConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lastConfigDirectory);
				fc.setFileFilter(new FileNameExtensionFilter("Configuration files", "cfg"));
				int returnVal = fc.showSaveDialog(window);
				if(returnVal == JFileChooser.CANCEL_OPTION){
					return;
				}

				File file = fc.getSelectedFile();

				lastConfigDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if(!fc.getFileFilter().accept(file))
						file = new File(file.toString()+".cfg");

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

		fileEditExecutions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditExecutionsDialog dlg = new EditExecutionsDialog(window, executions);
				dlg.setModalityType(ModalityType.APPLICATION_MODAL);
				dlg.setLocationRelativeTo(window);
				dlg.setVisible(true);
			}
		});

		fileChangeK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean valid = false;
				while(!valid){
					try{
						int k = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter K value:", "Set K", JOptionPane.PLAIN_MESSAGE));
						if(k>0){
							valid = true;
							KTailsAlgorithm.k = k;
						}
					}catch(Exception ex){
						valid = false;
					}
				}
			}
		});

		menuBar.add(fileMenu);
		menuBar.add(displayMenu);

		tree = new JTree(new DefaultMutableTreeNode(new JarTreeItem("No file loaded")));
		tree.setCellRenderer(new ClassTreeCellRenderer());
		tree.setCellEditor(new ClassTreeCellEditor());
		tree.setEditable(true);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setPreferredSize(new Dimension(300, 1));

		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				selectedPath = selPath;
				if(selRow != -1) {
					if(e.getClickCount() == 1) {
						if(e.getButton() == MouseEvent.BUTTON3){
							treePopup.show(tree, e.getX(), e.getY());
						}
					}
					else if(e.getClickCount() == 2) {
						if(e.getButton() == MouseEvent.BUTTON1){
							if(tree.isCollapsed(selPath)){
								tree.expandPath(selPath);
							}else{
								tree.collapsePath(selPath);
							}
						}
					}
				}
			}
		};
		tree.addMouseListener(ml);

		cmbAlgorithm = new JComboBox<AlgorithmComboBoxWrapper>();
		for(Class<? extends VisualizationAlgorithm> algClass : VisualizationAlgorithm.ALGORITHMS) {
			cmbAlgorithm.addItem(new AlgorithmComboBoxWrapper(algClass));
		}
		cmbAlgorithm.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() != ItemEvent.SELECTED)
					return;

				boolean supportsIncremental = IncrementalVisualizationAlgorithm.class.isAssignableFrom(getSelectedAlgorithmClass());
				chkContinuousUpdating.setEnabled(supportsIncremental);

				if(AUTO_RUN)
					doTraceAndAnalysis();
			}
		});

		chkContinuousUpdating = new JCheckBox("Continuously update");
		saveTracesCheckbox = new JCheckBox("Save trace");


		runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doTraceAndAnalysis();
			}
		});

		configPanel = new JPanel();
		configPanel.setLayout(new GridBagLayout());
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridheight = gbc.gridwidth = 1;
			gbc.gridx = gbc.gridy = 0;
			gbc.weightx = gbc.weighty = 1;

			gbc.anchor = GridBagConstraints.EAST;
			gbc.fill = GridBagConstraints.VERTICAL;
			configPanel.add(new JLabel("Algorithm:"), gbc);
			gbc.gridx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.BOTH;
			configPanel.add(cmbAlgorithm, gbc);

			gbc.gridx = 0;
			gbc.gridy++; gbc.gridwidth = 2;
			configPanel.add(chkContinuousUpdating, gbc);

			gbc.gridy++;
			configPanel.add(saveTracesCheckbox, gbc);

			gbc.gridx = 0;
			gbc.gridy++; gbc.gridwidth = 2;
			configPanel.add(runButton, gbc);
		}

		treePanel = new JPanel();
		treePanel.setLayout(new BorderLayout());
		treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
		treePanel.add(configPanel, BorderLayout.SOUTH);

		graphPane = new ImagePane();

		window.setLayout(new BorderLayout());
		window.add(menuBar, BorderLayout.NORTH);
		window.add(graphPane, BorderLayout.CENTER);
		window.add(treePanel, BorderLayout.WEST);

		window.pack();
		window.setLocationRelativeTo(null);
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);


		// for testing
		/*try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("test.cfg")))) {
			TracerConfiguration conf = (TracerConfiguration)in.readObject();
			loadFromConfiguration(conf);
			saveTracesCheckbox.setSelected(true);
			doTraceAndAnalysis();

		} catch(IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}*/

	}

	private void checkAllBoxes(TreePath selPath, boolean check){
		Enumeration<TreeNode> children = ((DefaultMutableTreeNode)selPath.getLastPathComponent()).breadthFirstEnumeration();
		while (children.hasMoreElements()) {
			TreeNode child = children.nextElement();
			Object currentNode = ((DefaultMutableTreeNode) child).getUserObject();
			//cast your currentNode to the check box and set selected or unselected.
			if(currentNode instanceof AbstractTreeItem && ((AbstractTreeItem)currentNode).isCheckable()){
				((AbstractTreeItem)currentNode).checked = check;
			}
		}
		tree.repaint();
	}

	private Class<? extends VisualizationAlgorithm> getSelectedAlgorithmClass() {
		return ((AlgorithmComboBoxWrapper)cmbAlgorithm.getSelectedItem()).algClass;
	}

	private VisualizationAlgorithm getSelectedAlgorithmInstance() {
		return ((AlgorithmComboBoxWrapper)cmbAlgorithm.getSelectedItem()).createInstance();
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

	private TraceMethodFilter getSelectedMethodFilter() {
		return new TraceMethodFilter() {
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
	}

	private TraceFieldFilter getSelectedFieldFilter() {
		return new TraceFieldFilter() {
			private Set<String> selectedFields = new HashSet<String>();

			{
				for(FieldTreeItem i : allFieldTreeItems) {
					if(i.checked) {
						FieldKey fk = new FieldKey(i.field);
						selectedFields.add(fk.className+"."+fk.name);
					}
				}
			}

			@Override
			public boolean isFieldTraced(com.sun.jdi.Field f) {
				return selectedFields.contains(f.declaringType().name()+"."+f.name());
			}
		};
	}

	private void doTraceAndAnalysis() {
		if(jarData == null)
			return;

		final TraceMethodFilter methodFilter = getSelectedMethodFilter();
		final TraceFieldFilter fieldFilter = getSelectedFieldFilter();

		final String path = jarData.file.getAbsolutePath();
		final String mainClass = jarData.manifest.getMainAttributes().getValue(Name.MAIN_CLASS);

		final ExecutionData[] executionsArray = executions.toArray(new ExecutionData[executions.size()]);

		final VisualizationAlgorithm algorithm = getSelectedAlgorithmInstance();

		final boolean useIncrementalUpdating = chkContinuousUpdating.isSelected();
		final File savedTraceFile = saveTracesCheckbox.isSelected() ? chooseSavedTraceFile() : null;

		if(saveTracesCheckbox.isSelected() && savedTraceFile == null) {
			return;
		}

		final Set<String> loadedClasses = new HashSet<String>();
		for(Class<?> cl : jarData.data)
			loadedClasses.add(cl.getName());

		Thread thread = new Thread() {

			@Override
			public void run() {

				try {

					// The filters to use when tracing
					TraceMethodFilter initialMethodFilter;
					TraceFieldFilter initialFieldFilter;

					if(savedTraceFile == null) {
						initialMethodFilter = methodFilter;
						initialFieldFilter = fieldFilter;
					} else {
						initialMethodFilter = new TraceMethodFilter() {
							@Override
							public boolean isMethodTraced(com.sun.jdi.Method m) {
								return loadedClasses.contains(m.declaringType().name());
							}
						};
						initialFieldFilter = new TraceFieldFilter() {
							@Override
							public boolean isFieldTraced(com.sun.jdi.Field f) {
								return loadedClasses.contains(f.declaringType().name());
							}
						};
					}

					if(useIncrementalUpdating && algorithm instanceof IncrementalVisualizationAlgorithm && executionsArray.length == 1) {

						ExecutionData ed = executionsArray[0];

						final IncrementalVisualizationAlgorithm iva = (IncrementalVisualizationAlgorithm)algorithm;

						iva.startIncremental();

						Tracer.launchAndTraceAsync("-cp \"" + path + "\"", mainClass+" "+ed.commandLineArguments, methodFilter, fieldFilter, new RealtimeTraceConsumer() {

							@Override
							public void onTracerCrash(Throwable t) {
								t.printStackTrace();
							}

							@Override
							public void onTraceLine(String line) {
								if(iva.processLine(line)) {
									Graph graph = iva.getCurrentGraph();

									try {
										File pngfile = new File("tempAnalysis.png");
										GraphSaver.save(graph, pngfile);
										final BufferedImage image = ImageIO.read(pngfile);

										EventQueue.invokeLater(new Runnable() {
											@Override
											public void run() {
												graphPane.setImage(image);
											}
										});
									} catch(Exception e) {
										e.printStackTrace();
									}
								}
							}

							@Override
							public void onTraceFinish() {

							}
						});

					} else {
						Trace[] traces = new Trace[executionsArray.length];

						for(int k = 0; k < executions.size(); k++) {
							ExecutionData ed = executions.get(k);

							FutureTraceConsumer future = new FutureTraceConsumer();
							Tracer.launchAndTraceAsync("-cp \"" + path + "\"", mainClass+" "+ed.commandLineArguments, initialMethodFilter, initialFieldFilter, future);
							traces[k] = future.get();
						}

						if(savedTraceFile != null) {
							TraceFile tf = new TraceFile();
							tf.traces = traces;
							tf.config = new TracerConfiguration();
							saveToConfiguration(tf.config);
							tf.write(savedTraceFile);
						}

						processTraces(traces);

					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.setName("MainWindow tracer thread");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Called after a set of traces is either traced, or loaded from a file.
	 * @param traces The traces.
	 * @param algorithm The algorithm to use.
	 */
	private void processTraces(Trace[] traces) throws IOException, InterruptedException {
		VisualizationAlgorithm algorithm = getSelectedAlgorithmInstance();
		Graph graph = algorithm.generateGraph(traces);

		File pngfile = new File("tempAnalysis.png");
		GraphSaver.save(graph, pngfile);
		final BufferedImage image = ImageIO.read(pngfile);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				graphPane.setImage(image);
			}
		});
	}


	/**
	 * Asks the user to choose a trace file to save to.
	 * @return The file the user selected, or null if they cancelled.
	 */
	private File chooseSavedTraceFile() {
		JFileChooser fc = new JFileChooser(lastTraceDirectory);
		fc.setFileFilter(new FileNameExtensionFilter("Trace files", "trace"));

		if(fc.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			if(!fc.getFileFilter().accept(file))
				file = new File(file.toString()+".trace");

			lastTraceDirectory = file.getParentFile();

			return file;

		} else {
			return null;
		}
	}

	public void saveToConfiguration(TracerConfiguration conf) {
		conf.jarFile = jarData.file;
		for(MethodTreeItem mti : allMethodTreeItems)
			conf.selectedMethods.put(mti.method, mti.checked);
		for(FieldTreeItem fti : allFieldTreeItems)
			conf.selectedFields.put(new FieldKey(fti.field), fti.checked);

		AlgorithmComboBoxWrapper algorithm = (AlgorithmComboBoxWrapper)cmbAlgorithm.getSelectedItem();
		conf.algorithmName = algorithm.name;
		conf.algorithmClassName = algorithm.algClass.getName();

		conf.executions = executions;
		conf.displayID = GraphSaver.displayID;
		conf.displayState = GraphSaver.displayState;
		conf.displayClass = GraphSaver.displayClass;
		conf.displayMethod = GraphSaver.displayMethod;
		conf.displayParams = GraphSaver.displayParams;

		conf.k = KTailsAlgorithm.k;

		conf.continuousUpdating = chkContinuousUpdating.isSelected();
	}

	public void loadFromConfiguration(TracerConfiguration conf) {
		loadJarFile(conf.jarFile);

		for(MethodTreeItem mti : allMethodTreeItems) {
			Boolean saved = conf.selectedMethods.get(mti.method);
			mti.checked = (saved != null ? saved : DEFAULT_METHOD_SELECTED);
		}

		for(FieldTreeItem fti : allFieldTreeItems) {
			Boolean saved = conf.selectedFields.get(new FieldKey(fti.field));
			fti.checked = (saved != null ? saved : DEFAULT_FIELD_SELECTED);
		}

		boolean foundAlgorithm = false;
		// Try to find an algorithm by class name first
		for(int k = 0; k < cmbAlgorithm.getItemCount() && !foundAlgorithm; k++) {
			AlgorithmComboBoxWrapper acbw = (AlgorithmComboBoxWrapper)cmbAlgorithm.getItemAt(k);
			if(acbw.algClass.getName().equals(conf.algorithmClassName)) {
				foundAlgorithm = true;
				cmbAlgorithm.setSelectedIndex(k);
			}
		}
		// then try the name if the class name fails (maybe because the class name was changed)
		for(int k = 0; k < cmbAlgorithm.getItemCount() && !foundAlgorithm; k++) {
			AlgorithmComboBoxWrapper acbw = (AlgorithmComboBoxWrapper)cmbAlgorithm.getItemAt(k);
			if(acbw.name.equals(conf.algorithmName)) {
				foundAlgorithm = true;
				cmbAlgorithm.setSelectedIndex(k);
			}
		}
		// If the algorithm the configuration was saved with is not available, just pick the first one.
		if(!foundAlgorithm)
			cmbAlgorithm.setSelectedIndex(0);


		//Set display settings
		GraphSaver.displayID = conf.displayID;
		displayID.setState(GraphSaver.displayID);
		GraphSaver.displayState = conf.displayState;
		displayState.setState(GraphSaver.displayState);
		GraphSaver.displayClass = conf.displayClass;
		displayClass.setState(GraphSaver.displayClass);
		GraphSaver.displayMethod = conf.displayMethod;
		displayMethod.setState(GraphSaver.displayMethod);
		GraphSaver.displayParams = conf.displayParams;
		displayParams.setState(GraphSaver.displayParams);

		KTailsAlgorithm.k = conf.k;

		executions = new ArrayList<ExecutionData>(conf.executions);
		if(executions.size() == 0)
			executions.add(new ExecutionData());

		chkContinuousUpdating.setSelected(conf.continuousUpdating);

		if(AUTO_RUN)
			doTraceAndAnalysis();
	}


	private List<MethodTreeItem> allMethodTreeItems = new ArrayList<MethodTreeItem>();
	private List<FieldTreeItem> allFieldTreeItems = new ArrayList<FieldTreeItem>();


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
			allFieldTreeItems.add(fti);
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
						if(AUTO_RUN)
							doTraceAndAnalysis();
					}
				});
			}
			return rv;
		}

	}
}
