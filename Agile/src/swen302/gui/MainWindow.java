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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.JSplitPane;
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
import swen302.automaton.AlgorithmFinder;
import swen302.automaton.AlgorithmParameters;
import swen302.automaton.IncrementalVisualizationAlgorithm;
import swen302.automaton.VisualizationAlgorithm;
import swen302.execution.ExecutionData;
import swen302.graph.Graph;
import swen302.graph.LabelFormatOptions;
import swen302.gui.classtree.AbstractTreeItem;
import swen302.gui.classtree.ClassTreeItem;
import swen302.gui.classtree.FieldInGroupTreeItem;
import swen302.gui.classtree.FieldTreeItem;
import swen302.gui.classtree.GroupTreeItem;
import swen302.gui.classtree.JarTreeItem;
import swen302.gui.classtree.MethodTreeItem;
import swen302.gui.classtree.PackageTreeItem;
import swen302.gui.classtree.ParameterTreeItem;
import swen302.gui.graphlayouts.EadesSpringEmbedder;
import swen302.tracer.FieldKey;
import swen302.tracer.FutureTraceConsumer;
import swen302.tracer.MethodKey;
import swen302.tracer.ParameterKey;
import swen302.tracer.RealtimeTraceConsumer;
import swen302.tracer.Trace;
import swen302.tracer.TraceEntry;
import swen302.tracer.TraceFilter;
import swen302.tracer.Tracer;

public class MainWindow {

	/** Whether methods are selected by default */
	public static final boolean DEFAULT_METHOD_SELECTED = false;
	/** Whether fields are selected by default */
	public static final boolean DEFAULT_FIELD_SELECTED = true;
	/** Whether parameters are selected by default */
	public static final boolean DEFAULT_PARAMETER_SELECTED = false;

	/**
	 * Whether the tracing and analysis will be run whenever the filter is
	 * changed.
	 */
	public static final boolean AUTO_RUN = false;

	private JFrame window;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem fileLoadJAR, fileLoadTrace, fileLoadAdvanced,
			fileEditExecutions, fileLoadConfig, fileSaveConfig, fileExit,
			fileChangeK;
	private JMenu displayMenu;
	private JCheckBoxMenuItem displayID, displayState, displayClass,
			displayMethod, displayParamTypes, displayParamValues;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JPanel treePanel;
	private JPanel configPanel;
	private JPanel graphConfigPanel;
	private VertexGraphPane graphPane;
	private JPopupMenu treePopup;
	private JMenuItem popupSelect, popupDeselect, popupAddGroup;
	private JLabel currentTraceFileLabel;
	private JComboBox<AlgorithmComboBoxWrapper> cmbAlgorithm;
	private JCheckBox chkContinuousUpdating;
	private JCheckBox saveTracesCheckbox;
	private JButton runButton;
	private SliderTextBox electricStrengthSlider, springStrengthSlider,
			springLengthSlider;
	private JSplitPane splitter;

	private JarData jarData;
	private File openTraceFile;

	private File lastJarDirectory = new File(".");
	private File lastConfigDirectory = new File(".");
	private File lastTraceDirectory = new File(".");

	private TreePath selectedPath, draggedPath;

	private List<ExecutionData> executions = new ArrayList<>(
			Arrays.asList(new ExecutionData()));

	//private MiniMap minimap;

	/**
	 * Instances of this are used in the combo box's item list, as they
	 * implement toString.
	 *
	 * @author campbealex2
	 */
	private static class AlgorithmComboBoxWrapper {
		Class<? extends VisualizationAlgorithm> algClass;
		String name;

		AlgorithmComboBoxWrapper(
				Class<? extends VisualizationAlgorithm> algClass) {
			this.algClass = algClass;
			try {
				name = algClass.newInstance().toString();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		VisualizationAlgorithm createInstance() {
			try {
				return algClass.newInstance();
			} catch (Exception e) {
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

		displayID = new JCheckBoxMenuItem("ID", true);
		displayMenu.add(displayID);
		displayState = new JCheckBoxMenuItem("State", true);
		displayMenu.add(displayState);
		displayClass = new JCheckBoxMenuItem("Class", true);
		displayMenu.add(displayClass);
		displayMethod = new JCheckBoxMenuItem("Method", true);
		displayMenu.add(displayMethod);
		displayParamTypes = new JCheckBoxMenuItem("Parameter types", true);
		displayMenu.add(displayParamTypes);
		displayParamValues = new JCheckBoxMenuItem("Parameter values", true);
		displayMenu.add(displayParamValues);

		treePopup = new JPopupMenu();
		popupSelect = treePopup.add("Select All");
		popupDeselect = treePopup.add("Deselect All");
		popupAddGroup = treePopup.add("Add group");


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

		popupAddGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				DefaultMutableTreeNode selectedNode = getSelectedTreeNode();

				GroupTreeItem groupNode = new GroupTreeItem(((ClassTreeItem)selectedNode).getTreeClass());

				allGroupTreeItems.add(groupNode);

				treeModel.insertNodeInto(groupNode, selectedNode, selectedNode.getChildCount());
			}
		});

		displayID.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LabelFormatOptions.displayID = displayID.isSelected();
				graphPane.onLabelsChanged();
			}
		});

		displayState.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LabelFormatOptions.displayState = displayState.isSelected();
				graphPane.onLabelsChanged();
			}
		});

		displayClass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LabelFormatOptions.displayClass = displayClass.isSelected();
				graphPane.onLabelsChanged();
			}
		});

		displayMethod.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LabelFormatOptions.displayMethod = displayMethod.isSelected();
				graphPane.onLabelsChanged();
			}
		});

		displayParamTypes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LabelFormatOptions.displayParamTypes = displayParamTypes
						.isSelected();
				graphPane.onLabelsChanged();
			}
		});

		displayParamValues.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LabelFormatOptions.displayParamValues = displayParamValues
						.isSelected();
				graphPane.onLabelsChanged();
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
				fc.setFileFilter(new FileNameExtensionFilter("JAR files",
						"zip", "jar"));
				int returnVal = fc.showOpenDialog(window);

				lastJarDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					loadJarFile(fc.getSelectedFile());

					executions.clear();
					executions.add(new ExecutionData());

					if (AUTO_RUN)
						doTraceAndAnalysis();
					else
						graphPane.setGraph(null);
				}
			}
		});

		fileLoadTrace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lastTraceDirectory);
				fc.setFileFilter(new FileNameExtensionFilter("Trace files",
						"trace"));
				int returnVal = fc.showOpenDialog(window);

				lastTraceDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						TraceFile tf = TraceFile.read(fc.getSelectedFile());
						loadFromConfiguration(tf.config);

						processTraces(tf.traces);

						openTraceFile = fc.getSelectedFile();

						updateCheckboxesEnabled();

						((DefaultMutableTreeNode) treeModel.getRoot())
								.setUserObject(new JarTreeItem(openTraceFile
										.getName()));
					} catch (IOException | InterruptedException exc) {
						throw new RuntimeException(exc);
					}
				}
			}
		});

		fileSaveConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lastConfigDirectory);
				fc.setFileFilter(new FileNameExtensionFilter(
						"Configuration files", "cfg"));
				int returnVal = fc.showSaveDialog(window);
				if (returnVal == JFileChooser.CANCEL_OPTION) {
					return;
				}

				File file = fc.getSelectedFile();

				lastConfigDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (!fc.getFileFilter().accept(file))
						file = new File(file.toString() + ".cfg");

					TracerConfiguration conf = new TracerConfiguration();
					saveToConfiguration(conf);
					try (ObjectOutputStream out = new ObjectOutputStream(
							new BufferedOutputStream(new FileOutputStream(file)))) {
						out.writeObject(conf);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		fileLoadConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lastConfigDirectory);
				fc.setFileFilter(new FileNameExtensionFilter(
						"Configuration files", "cfg"));
				int returnVal = fc.showOpenDialog(window);

				File file = fc.getSelectedFile();

				lastConfigDirectory = fc.getCurrentDirectory();

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try (ObjectInputStream in = new ObjectInputStream(
							new BufferedInputStream(new FileInputStream(file)))) {
						TracerConfiguration conf = (TracerConfiguration) in
								.readObject();
						loadFromConfiguration(conf);

					} catch (IOException | ClassNotFoundException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		fileEditExecutions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditExecutionsDialog dlg = new EditExecutionsDialog(window,
						executions);
				dlg.setModalityType(ModalityType.APPLICATION_MODAL);
				dlg.setLocationRelativeTo(window);
				dlg.setVisible(true);
			}
		});

		fileChangeK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean valid = false;
				while (!valid) {
					try {
						int k = Integer.parseInt(JOptionPane.showInputDialog(
								null, "Enter K value:", "Set K",
								JOptionPane.PLAIN_MESSAGE));
						if (k > 0) {
							valid = true;
							AlgorithmParameters.K = k;
						}
					} catch (Exception ex) {
						valid = false;
					}
				}
			}
		});

		menuBar.add(fileMenu);
		menuBar.add(displayMenu);

		treeModel = new DefaultTreeModel(new JarTreeItem("No file loaded"));
		tree = new JTree(treeModel);
		tree.setCellRenderer(new ClassTreeCellRenderer());
		tree.setCellEditor(new ClassTreeCellEditor());
		tree.setEditable(true);

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setPreferredSize(new Dimension(300, 1));

		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				tree.requestFocusInWindow();

				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				selectedPath = selPath;
				draggedPath = selPath;
				if (selRow != -1) {
					if (e.getClickCount() == 1) {
						if (e.getButton() == MouseEvent.BUTTON3) {
							AbstractTreeItem item = getSelectedTreeNode();

							popupAddGroup.setEnabled(item instanceof ClassTreeItem);

							treePopup.show(tree, e.getX(), e.getY());
						}
					} else if (e.getClickCount() == 2) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							if (tree.isCollapsed(selPath)) {
								tree.expandPath(selPath);
							} else {
								tree.collapsePath(selPath);
							}
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				TreePath dropPath = selPath;

				if (selRow != -1 && draggedPath != null) {
					if (e.getButton() == MouseEvent.BUTTON1) {

						AbstractTreeItem draggedItem = (AbstractTreeItem)draggedPath.getLastPathComponent();
						AbstractTreeItem dropItem = (AbstractTreeItem)dropPath.getLastPathComponent();

						if(draggedItem instanceof FieldTreeItem && (dropItem instanceof ClassTreeItem || dropItem instanceof GroupTreeItem)) {

							Class<?> dropClass;

							if(dropItem instanceof ClassTreeItem) {
								dropClass = ((ClassTreeItem)dropItem).getTreeClass();
							} else if(dropItem instanceof GroupTreeItem) {
								dropClass = ((GroupTreeItem)dropItem).getOwnerClass();
							} else {
								dropClass = null;
							}

							if(draggedItem.isCheckable() && dropClass != null && dropClass == ((FieldTreeItem)draggedItem).field.getDeclaringClass()) {

								FieldInGroupTreeItem figNode = new FieldInGroupTreeItem(((FieldTreeItem)draggedItem).field);

								treeModel.insertNodeInto(figNode, dropItem, dropItem.getChildCount());

								tree.expandPath(new TreePath(treeModel.getPathToRoot(dropItem)));
							}
						}
					}
				}

				draggedPath = null;
			}
		};
		tree.addMouseListener(ml);

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() != KeyEvent.VK_DELETE)
					return;

				TreePath selPath = tree.getSelectionPath();
				if(selPath == null)
					return;

				AbstractTreeItem node = (AbstractTreeItem)selPath.getLastPathComponent();
				if(node instanceof FieldInGroupTreeItem) {
					treeModel.removeNodeFromParent(node);
				}
				if(node instanceof GroupTreeItem) {
					treeModel.removeNodeFromParent(node);
					allGroupTreeItems.remove(node);
				}
			}
		});

		cmbAlgorithm = new JComboBox<AlgorithmComboBoxWrapper>();
		for (Class<? extends VisualizationAlgorithm> algClass : AlgorithmFinder.getAlgorithmClasses()) {
			cmbAlgorithm.addItem(new AlgorithmComboBoxWrapper(algClass));
		}
		cmbAlgorithm.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;

				updateCheckboxesEnabled();

				if (AUTO_RUN)
					doTraceAndAnalysis();
			}
		});

		currentTraceFileLabel = new JLabel("No file selected");
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

			gbc.gridwidth = 2;
			configPanel.add(currentTraceFileLabel, gbc);

			gbc.gridwidth = 1;
			gbc.gridy++;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.fill = GridBagConstraints.VERTICAL;
			configPanel.add(new JLabel("Algorithm:"), gbc);
			gbc.gridx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.BOTH;
			configPanel.add(cmbAlgorithm, gbc);

			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = 2;
			configPanel.add(chkContinuousUpdating, gbc);

			gbc.gridy++;
			configPanel.add(saveTracesCheckbox, gbc);

			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = 2;
			configPanel.add(runButton, gbc);
		}

		final double DEFAULT_MAGNETIC_STRENGTH = 350000;
		electricStrengthSlider = new SliderTextBox("Magnetic strength", 0,
				DEFAULT_MAGNETIC_STRENGTH * 20, DEFAULT_MAGNETIC_STRENGTH) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onChanged(double value) {
				EadesSpringEmbedder.MAGNETIC_STRENGTH = value;
			}
		};

		final double DEFAULT_SPRING_STRENGTH = 1.6;
		springStrengthSlider = new SliderTextBox("Spring strength", 0,
				DEFAULT_SPRING_STRENGTH * 4, DEFAULT_SPRING_STRENGTH) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onChanged(double value) {
				EadesSpringEmbedder.SPRING_STRENGTH = -value;
			}
		};

		final double DEFAULT_SPRING_LENGTH = 100;
		springLengthSlider = new SliderTextBox("Spring length", 0, 500,
				DEFAULT_SPRING_LENGTH) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onChanged(double value) {
				EadesSpringEmbedder.SPRING_LENGTH = value;
			}
		};

		//minimap = new MiniMap();
		//minimap.setPreferredSize(new Dimension(300, 300));
		//minimap.setMinimumSize(new Dimension(300, 300));
		//minimap.setMaximumSize(new Dimension(300, 300));

		graphConfigPanel = new JPanel();
		graphConfigPanel.setBorder(BorderFactory
				.createEmptyBorder(3, 10, 3, 10));
		graphConfigPanel.setLayout(new BoxLayout(graphConfigPanel,
				BoxLayout.Y_AXIS));
		graphConfigPanel.add(electricStrengthSlider);
		graphConfigPanel.add(springStrengthSlider);
		graphConfigPanel.add(springLengthSlider);
		graphConfigPanel.add(Box.createVerticalGlue());
		//graphConfigPanel.add(minimap);


		treePanel = new JPanel();
		treePanel.setLayout(new BorderLayout());
		tree.setPreferredSize(null);
		tree.setMinimumSize(null);
		tree.setMaximumSize(null);
		treePanel.add(new JScrollPane(tree,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.CENTER);
		treePanel.add(configPanel, BorderLayout.SOUTH);

		graphPane = new VertexGraphPane(/*this.minimap*/);

		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.add(treePanel);
		splitter.add(graphPane);

		window.setLayout(new BorderLayout());
		window.add(menuBar, BorderLayout.NORTH);
		window.add(splitter, BorderLayout.CENTER);
		window.add(graphConfigPanel, BorderLayout.EAST);

		window.pack();
		window.setLocationRelativeTo(null);
		window.setExtendedState(window.getExtendedState()
				| JFrame.MAXIMIZED_BOTH);

		updateCheckboxesEnabled();
	}

	private void updateCheckboxesEnabled() {
		boolean supportsIncremental = IncrementalVisualizationAlgorithm.class
				.isAssignableFrom(getSelectedAlgorithmClass());
		chkContinuousUpdating.setEnabled(jarData != null && supportsIncremental
				&& openTraceFile == null);

		saveTracesCheckbox.setEnabled(jarData != null && openTraceFile == null);

		if (jarData == null)
			currentTraceFileLabel.setText("No file selected.");
		else if (openTraceFile == null)
			currentTraceFileLabel.setText("Tracing: " + jarData.file.getName());
		else
			currentTraceFileLabel.setText("Using saved trace: "
					+ openTraceFile.getName());

		runButton.setEnabled(jarData != null);
	}

	private void checkAllBoxes(TreePath selPath, boolean check) {
		@SuppressWarnings("unchecked")
		Enumeration<TreeNode> children = ((DefaultMutableTreeNode) selPath
				.getLastPathComponent()).breadthFirstEnumeration();
		while (children.hasMoreElements()) {
			AbstractTreeItem child = (AbstractTreeItem)children.nextElement();
			// cast your child to the check box and set selected or
			// unselected.
			if(child.isCheckable()) {
				child.checked = check;
			}
		}
		tree.repaint();
	}

	private Class<? extends VisualizationAlgorithm> getSelectedAlgorithmClass() {
		return ((AlgorithmComboBoxWrapper) cmbAlgorithm.getSelectedItem()).algClass;
	}

	private VisualizationAlgorithm getSelectedAlgorithmInstance() {
		return ((AlgorithmComboBoxWrapper) cmbAlgorithm.getSelectedItem())
				.createInstance();
	}

	private void loadJarFile(File jarfile) {

		openTraceFile = null;

		jarData = JarLoader.loadJarFile(jarfile);

		updateCheckboxesEnabled();

		DefaultMutableTreeNode top = new JarTreeItem(jarfile.getName());
		((DefaultTreeModel) tree.getModel()).setRoot(top);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				tree.expandPath(new TreePath(new Object[] { treeModel.getRoot() }));
			}
		});

		createNodes(top, jarData.data);
	}

	public void setVisible(boolean visible) {
		window.setVisible(visible);
	}

	private TraceFilter getSelectionFilter() {
		return new TraceFilter() {
			private Set<MethodKey> selectedMethods = new HashSet<MethodKey>();
			private Set<FieldKey> selectedFields = new HashSet<FieldKey>();
			private Set<ParameterKey> selectedParameters = new HashSet<ParameterKey>();

			{
				for (MethodTreeItem i : allMethodTreeItems)
					if (i.checked)
						selectedMethods.add(i.method);

				for (FieldTreeItem i : allFieldTreeItems)
					if (i.checked)
						selectedFields.add(new FieldKey(i.field));

				for (ParameterTreeItem i : allParameterTreeItems)
					if (i.checked)
						selectedParameters.add(new ParameterKey(
								i.parent.method, i.argNum));
			}

			@Override
			public boolean isMethodTraced(MethodKey m) {
				return selectedMethods.contains(m);
			}

			@Override
			public boolean isFieldTraced(FieldKey f) {
				if(f.className.startsWith("__g"))
					return true;
				return selectedFields.contains(f);
			}

			@Override
			public boolean isParameterTraced(ParameterKey p) {
				return selectedParameters.contains(p);
			}
		};
	}

	private void doTraceAndAnalysis() {
		if (jarData == null)
			return;

		final TraceFilter filter = getSelectionFilter();

		final String path = jarData.file.getAbsolutePath();
		final String mainClass = jarData.manifest.getMainAttributes().getValue(
				Name.MAIN_CLASS);

		final ExecutionData[] executionsArray = executions
				.toArray(new ExecutionData[executions.size()]);

		final VisualizationAlgorithm algorithm = getSelectedAlgorithmInstance();

		if (openTraceFile != null) {
			try {
				TraceFile tf = TraceFile.read(openTraceFile);
				processTraces(tf.traces);
			} catch (IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}
			return;
		}

		final boolean useIncrementalUpdating = chkContinuousUpdating
				.isSelected();
		final File savedTraceFile = saveTracesCheckbox.isSelected() ? chooseSavedTraceFile()
				: null;

		if (saveTracesCheckbox.isSelected() && savedTraceFile == null) {
			return;
		}

		final Set<String> loadedClasses = new HashSet<String>();
		for (Class<?> cl : jarData.data)
			loadedClasses.add(cl.getName());

		final List<Group> groups = getConfiguredGroups();

		Thread thread = new Thread() {

			@Override
			public void run() {

				try {

					// The filters to use when tracing
					TraceFilter initialFilter;

					if (savedTraceFile == null) {
						initialFilter = addGroupContainedFieldsToFilter(filter, groups);
					} else {
						initialFilter = new TraceFilter() {
							@Override
							public boolean isMethodTraced(MethodKey m) {
								return loadedClasses.contains(m.className);
							}

							@Override
							public boolean isFieldTraced(FieldKey f) {
								return loadedClasses.contains(f.className);
							}

							@Override
							public boolean isParameterTraced(ParameterKey p) {
								return true;
							}
						};
					}

					if (useIncrementalUpdating
							&& algorithm instanceof IncrementalVisualizationAlgorithm
							&& executionsArray.length == 1) {

						ExecutionData ed = executionsArray[0];

						final IncrementalVisualizationAlgorithm iva = (IncrementalVisualizationAlgorithm) algorithm;

						iva.startIncremental();

						final Graph[] graphs = iva.getCurrentGraphs();

						setGraphs(graphs);

						Tracer.launchAndTraceAsync("-cp \"" + path + "\"",
								mainClass + " " + ed.commandLineArguments,
								filter, new RealtimeTraceConsumer() {

									@Override
									public void onTracerCrash(Throwable t) {
										t.printStackTrace();
									}

									@Override
									public void onTraceLine(TraceEntry line) {
										synchronized (graphs) {
											for(Group g : groups)
												g.updateLine(line);
											iva.processLine(line);
										}
									}

									@Override
									public void onTraceFinish() {

									}
								});

					} else {
						Trace[] traces = new Trace[executionsArray.length];

						for (int k = 0; k < executions.size(); k++) {
							ExecutionData ed = executions.get(k);

							FutureTraceConsumer future = new FutureTraceConsumer();
							Tracer.launchAndTraceAsync("-cp \"" + path + "\"",
									mainClass + " " + ed.commandLineArguments,
									initialFilter, future);
							traces[k] = future.get();
						}

						if (savedTraceFile != null) {
							TraceFile tf = new TraceFile();
							tf.traces = traces;
							tf.config = new TracerConfiguration();
							saveToConfiguration(tf.config);
							tf.write(savedTraceFile);
						}

						processTraces(traces);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.setName("MainWindow tracer thread");
		thread.setDaemon(true);
		thread.start();
	}

	protected TraceFilter addGroupContainedFieldsToFilter(final TraceFilter filter, List<Group> groups) {
		final Set<FieldKey> groupContainedFields = new HashSet<FieldKey>();
		for(Group g : groups)
			groupContainedFields.addAll(g.fields);
		return new TraceFilter() {
			@Override
			public boolean isMethodTraced(MethodKey m) {
				return filter.isMethodTraced(m);
			}

			@Override
			public boolean isFieldTraced(FieldKey f) {
				if(groupContainedFields.contains(f))
					return true;
				return filter.isFieldTraced(f);
			}

			@Override
			public boolean isParameterTraced(ParameterKey p) {
				return filter.isParameterTraced(p);
			}
		};
	}

	/**
	 * Called after a set of traces is either traced, or loaded from a file.
	 *
	 * @param traces
	 *            The traces.
	 * @param algorithm
	 *            The algorithm to use.
	 */
	private void processTraces(Trace[] traces) throws IOException,
			InterruptedException {

		TraceFilter filter = getSelectionFilter();
		List<Group> groups = getConfiguredGroups();

		for (Trace t : traces) {
			for(TraceEntry e : t.lines)
				for(Group g : groups)
					g.updateLine(e);

			t.applyFilter(filter);

			System.out.println(t.lines.get(0).state);
		}

		VisualizationAlgorithm algorithm = getSelectedAlgorithmInstance();
		final Graph[] graphs = algorithm.generateGraph(traces);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setGraphs(graphs);
			}
		});
	}

	protected void setGraphs(Graph[] graphs) {
		if(graphs.length == 1)
			graphPane.setGraph(graphs[0]);
		else
			throw new RuntimeException("Cannot display "+graphs.length+" graphs at a time"); // TODO: support multiple graphs
	}

	/**
	 * Asks the user to choose a trace file to save to.
	 *
	 * @return The file the user selected, or null if they cancelled.
	 */
	private File chooseSavedTraceFile() {
		JFileChooser fc = new JFileChooser(lastTraceDirectory);
		fc.setFileFilter(new FileNameExtensionFilter("Trace files", "trace"));

		if (fc.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			if (!fc.getFileFilter().accept(file))
				file = new File(file.toString() + ".trace");

			lastTraceDirectory = file.getParentFile();

			return file;

		} else {
			return null;
		}
	}

	public List<Group> getConfiguredGroups() {
		List<Group> groups = new ArrayList<Group>();
		for(GroupTreeItem gti : allGroupTreeItems) {
			Group g = new Group();

			for(int k = 0; k < gti.getChildCount(); k++) {
				FieldInGroupTreeItem child = (FieldInGroupTreeItem)gti.getChildAt(k);

				g.fields.add(child.getFieldKey());
			}
			groups.add(g);
		}
		return groups;
	}

	public void saveToConfiguration(TracerConfiguration conf) {
		conf.jarFile = jarData.file;
		for (MethodTreeItem mti : allMethodTreeItems)
			conf.selectedMethods.put(mti.method, mti.checked);
		for (FieldTreeItem fti : allFieldTreeItems)
			conf.selectedFields.put(new FieldKey(fti.field), fti.checked);
		for (ParameterTreeItem pti : allParameterTreeItems)
			conf.selectedParameters.put(new ParameterKey(pti.parent.method,
					pti.argNum), pti.checked);

		conf.groups = getConfiguredGroups();

		AlgorithmComboBoxWrapper algorithm = (AlgorithmComboBoxWrapper) cmbAlgorithm
				.getSelectedItem();
		conf.algorithmName = algorithm.name;
		conf.algorithmClassName = algorithm.algClass.getName();

		conf.executions = executions;
		conf.displayID = LabelFormatOptions.displayID;
		conf.displayState = LabelFormatOptions.displayState;
		conf.displayClass = LabelFormatOptions.displayClass;
		conf.displayMethod = LabelFormatOptions.displayMethod;
		conf.displayParams = LabelFormatOptions.displayParamTypes;
		conf.displayParamValues = LabelFormatOptions.displayParamValues;

		conf.haveGraphPhysicsSettings = true;
		conf.graphElectricStrength = electricStrengthSlider.getValue();
		conf.graphSpringStrength = springStrengthSlider.getValue();
		conf.graphSpringLength = springLengthSlider.getValue();

		conf.k = AlgorithmParameters.K;

		conf.continuousUpdating = chkContinuousUpdating.isSelected();
	}

	public ClassTreeItem getClassTreeItem(String name) {
		for(ClassTreeItem cti : allClassTreeItems)
			if(cti.getTreeClass().getName().equals(name))
				return cti;
		return null;
	}

	public void loadFromConfiguration(TracerConfiguration conf) {
		loadJarFile(conf.jarFile);

		for (MethodTreeItem mti : allMethodTreeItems) {
			Boolean saved = conf.selectedMethods.get(mti.method);
			mti.checked = (saved != null ? saved : DEFAULT_METHOD_SELECTED);
		}

		for (FieldTreeItem fti : allFieldTreeItems) {
			Boolean saved = conf.selectedFields.get(new FieldKey(fti.field));
			fti.checked = (saved != null ? saved : DEFAULT_FIELD_SELECTED);
		}

		for (ParameterTreeItem pti : allParameterTreeItems) {
			Boolean saved = conf.selectedParameters.get(new ParameterKey(
					pti.parent.method, pti.argNum));
			pti.checked = (saved != null ? saved : DEFAULT_PARAMETER_SELECTED);
		}

		if(conf.groups != null) {
			for(Group g : conf.groups) {
				if(g.fields.isEmpty())
					continue;

				String className = g.fields.get(0).className;
				ClassTreeItem cti = getClassTreeItem(className);
				if(cti == null)
					continue;

				GroupTreeItem gti = new GroupTreeItem(cti.getTreeClass());
				for(FieldKey field : g.fields) {
					try {
						FieldInGroupTreeItem figti = new FieldInGroupTreeItem(cti.getTreeClass().getDeclaredField(field.name));
						treeModel.insertNodeInto(figti, gti, gti.getChildCount());
					} catch(NoSuchFieldException e) {
						// Do nothing; don't insert field into group
					}
				}
				allGroupTreeItems.add(gti);
				treeModel.insertNodeInto(gti, cti, cti.getChildCount());
			}
		}

		boolean foundAlgorithm = false;
		// Try to find an algorithm by class name first
		for (int k = 0; k < cmbAlgorithm.getItemCount() && !foundAlgorithm; k++) {
			AlgorithmComboBoxWrapper acbw = (AlgorithmComboBoxWrapper) cmbAlgorithm
					.getItemAt(k);
			if (acbw.algClass.getName().equals(conf.algorithmClassName)) {
				foundAlgorithm = true;
				cmbAlgorithm.setSelectedIndex(k);
			}
		}
		// then try the name if the class name fails (maybe because the class
		// name was changed)
		for (int k = 0; k < cmbAlgorithm.getItemCount() && !foundAlgorithm; k++) {
			AlgorithmComboBoxWrapper acbw = (AlgorithmComboBoxWrapper) cmbAlgorithm
					.getItemAt(k);
			if (acbw.name.equals(conf.algorithmName)) {
				foundAlgorithm = true;
				cmbAlgorithm.setSelectedIndex(k);
			}
		}
		// If the algorithm the configuration was saved with is not available,
		// just pick the first one.
		if (!foundAlgorithm)
			cmbAlgorithm.setSelectedIndex(0);

		// Set display settings
		LabelFormatOptions.displayID = conf.displayID;
		displayID.setState(LabelFormatOptions.displayID);
		LabelFormatOptions.displayState = conf.displayState;
		displayState.setState(LabelFormatOptions.displayState);
		LabelFormatOptions.displayClass = conf.displayClass;
		displayClass.setState(LabelFormatOptions.displayClass);
		LabelFormatOptions.displayMethod = conf.displayMethod;
		displayMethod.setState(LabelFormatOptions.displayMethod);
		LabelFormatOptions.displayParamTypes = conf.displayParams;
		displayParamTypes.setState(LabelFormatOptions.displayParamTypes);
		LabelFormatOptions.displayParamValues = conf.displayParamValues;
		displayParamValues.setState(LabelFormatOptions.displayParamValues);

		// Set graph layout settings
		if (conf.haveGraphPhysicsSettings) {
			electricStrengthSlider.setValue(conf.graphElectricStrength, false);
			springStrengthSlider.setValue(conf.graphSpringStrength, false);
			springLengthSlider.setValue(conf.graphSpringLength, false);
		}

		AlgorithmParameters.K = conf.k;

		executions = new ArrayList<ExecutionData>(conf.executions);

		if (executions.size() == 0)
			executions.add(new ExecutionData());

		chkContinuousUpdating.setSelected(conf.continuousUpdating);

		if (AUTO_RUN)
			doTraceAndAnalysis();
	}

	private List<ClassTreeItem> allClassTreeItems = new ArrayList<ClassTreeItem>();
	private List<MethodTreeItem> allMethodTreeItems = new ArrayList<MethodTreeItem>();
	private List<FieldTreeItem> allFieldTreeItems = new ArrayList<FieldTreeItem>();
	private List<ParameterTreeItem> allParameterTreeItems = new ArrayList<ParameterTreeItem>();
	private List<GroupTreeItem> allGroupTreeItems = new ArrayList<GroupTreeItem>();

	private void createNodes(DefaultMutableTreeNode top,
			ArrayList<Class<?>> classData) {
		allMethodTreeItems.clear();
		allFieldTreeItems.clear();
		allParameterTreeItems.clear();
		allGroupTreeItems.clear();
		allClassTreeItems.clear();

		Map<String, DefaultMutableTreeNode> packages = new HashMap<>();

		Map<Class<?>, DefaultMutableTreeNode> classNodes = new HashMap<Class<?>, DefaultMutableTreeNode>();

		// For each class, generate the subtree corresponding to that class
		for (Class<?> data : classData)
			classNodes.put(data, createClassNodes(data));

		// Then insert the subtrees at the right locations (under packages or
		// other classes)
		for (Map.Entry<Class<?>, DefaultMutableTreeNode> entry : classNodes
				.entrySet()) {
			Class<?> clazz = entry.getKey();

			DefaultMutableTreeNode parentNode;

			Class<?> enclosingClass = clazz.getEnclosingClass();

			if (enclosingClass == null) {

				// Top-level class; insert under a package
				String packageName = getPackageName(clazz);

				DefaultMutableTreeNode packageNode = packages.get(packageName);
				if (packageNode == null) {
					// If no node exists for this package, create one
					packageNode = new PackageTreeItem(packageName);
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

	/**
	 * Returns the package name, as shown in the class tree - e.g.
	 * "swen302.testprograms" or "(default package)"
	 */
	private String getPackageName(Class<?> clazz) {
		String className = clazz.getName();
		if (className.contains("."))
			return className.substring(0, className.lastIndexOf('.'));
		else
			return "(default package)";
	}

	/** Creates a subtree of the class tree from one class. */
	private DefaultMutableTreeNode createClassNodes(Class<?> data) {
		ClassTreeItem classItem = new ClassTreeItem(data);

		allClassTreeItems.add(classItem);

		for (Field field : data.getDeclaredFields()) {
			FieldTreeItem fti = new FieldTreeItem(field);
			if (field.isSynthetic() && !fti.isCheckable())
				continue;
			allFieldTreeItems.add(fti);
			classItem.add(fti);
		}

		for (Method method : data.getDeclaredMethods()) {
			MethodTreeItem methodNode = new MethodTreeItem(classItem,
					new MethodKey(method), method);
			if (method.isSynthetic() && !methodNode.isCheckable())
				continue;

			classItem.add(methodNode);

			allMethodTreeItems.add(methodNode);

			int numArgs = method.getParameterTypes().length;
			for (int k = 0; k < numArgs; k++) {
				ParameterTreeItem pti = new ParameterTreeItem(methodNode, k);
				allParameterTreeItems.add(pti);
				methodNode.add(pti);
			}
		}
		return classItem;
	}

	private AbstractTreeItem getSelectedTreeNode() {
		return (AbstractTreeItem)selectedPath.getLastPathComponent();
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
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			AbstractTreeItem item = (AbstractTreeItem)value;
			if (item.isCheckable()) {
				checkBoxPanel.label.setIcon(item.getIcon());
				checkBoxPanel.checkBox.setSelected(item.checked);
				checkBoxPanel.checkBox.setText(value.toString());
				return checkBoxPanel;
			} else {
				label.setIcon(item.getIcon());
				label.setText(value.toString());
				return label;
			}
		}
	}

	private class ClassTreeCellEditor extends AbstractCellEditor implements
			TreeCellEditor {
		private static final long serialVersionUID = 1L;

		Object value;

		@Override
		public Object getCellEditorValue() {
			return value;
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree,
				final Object value_, boolean selected, boolean expanded,
				boolean leaf, int row) {
			final Component rv = new ClassTreeCellRenderer()
					.getTreeCellRendererComponent(tree, value_, selected,
							expanded, leaf, row, true);
			this.value = (AbstractTreeItem)value_;
			if (rv instanceof CheckBoxIconPanel) {
				((CheckBoxIconPanel) rv).checkBox
						.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent e) {
								if (stopCellEditing())
									fireEditingStopped();
								((AbstractTreeItem) value).checked = ((CheckBoxIconPanel) rv).checkBox
										.isSelected();
								if (AUTO_RUN)
									doTraceAndAnalysis();
							}
						});
			}
			return rv;
		}

	}
}
