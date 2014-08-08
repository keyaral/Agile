package swen302.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import swen302.execution.ExecutionData;

/**
 * This dialog allows the user to edit the executions.
 *
 * @see ExecutionData
 *
 * @author campbealex2
 */
public class EditExecutionsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private List<ExecutionData> list;

	private JList<ExecutionData> listControl;
	private JPanel listPanel;
	private JPanel detailsPanel;
	private JButton okButton, cancelButton;
	private JButton addButton, removeButton;
	private JTextField argumentsField;

	private ListModel listModel;

	private class ListModel extends AbstractListModel<ExecutionData> {
		private static final long serialVersionUID = 1L;

		@Override
		public ExecutionData getElementAt(int index) {
			return list.get(index);
		}
		@Override
		public int getSize() {
			return list.size();
		}

		public void remove(int index) {
			if(index < 0 || index >= list.size())
				return;

			list.remove(index);
			fireIntervalRemoved(this, index, index+1);
		}

		/** Returns the new index. */
		public int add() {
			list.add(new ExecutionData());
			fireIntervalAdded(this, list.size()-1, list.size());
			return list.size() - 1;
		}

		public void elementChanged(int index) {
			fireContentsChanged(this, index, index+1);
		}
	}

	public EditExecutionsDialog(Window owner, final List<ExecutionData> originalList) {
		super(owner, "Edit executions");

		this.list = new ArrayList<>();
		for(ExecutionData ed : originalList)
			this.list.add(ed.clone());

		listModel = new ListModel();
		listControl = new JList<>(listModel);
		listControl.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		listControl.setPreferredSize(new Dimension(250, 500));

		addButton = new JButton("Add");
		removeButton = new JButton("Remove");

		argumentsField = new JTextField("ASDF");

		listPanel = new JPanel();
		listPanel.setLayout(new GridBagLayout());
		listPanel.add(listControl, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		listPanel.add(addButton, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		listPanel.add(removeButton, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		detailsPanel = new JPanel();
		detailsPanel.setLayout(new GridBagLayout());
		detailsPanel.add(new JLabel("Arguments"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(10, 10, 0, 0), 0, 0));
		detailsPanel.add(argumentsField, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		detailsPanel.add(new JPanel(), new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));

		Container root = new JPanel();
		root.setLayout(new GridLayout(1, 2));
		root.add(listPanel);
		root.add(detailsPanel);

		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				originalList.clear();
				originalList.addAll(EditExecutionsDialog.this.list);

				EditExecutionsDialog.this.setVisible(false);
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditExecutionsDialog.this.setVisible(false);
			}
		});

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listControl.setSelectedIndex(listModel.add());
			}
		});

		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = listControl.getSelectedIndex();
				listModel.remove(index);
				if(list.size() > 0) {
					index--;
					if(index < 0)
						index = 0;
					listControl.setSelectedIndex(index);
				}
			}
		});

		listControl.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int sel = listControl.getSelectedIndex();

				if(sel == -1) {
					argumentsField.setEnabled(false);

				} else {
					argumentsField.setEnabled(true);
					updateDetailsFromList(listControl.getSelectedIndex());
				}
			}
		});

		listControl.setSelectedIndex(0);

		argumentsField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateListFromDetails(listControl.getSelectedIndex());
					}
				});
			}

			@Override public void keyReleased(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {}
		});



		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(root, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		pack();
	}

	private void updateDetailsFromList(int index) {
		if(index < 0 || index >= list.size())
			return;

		ExecutionData data = list.get(index);
		argumentsField.setText(data.commandLineArguments);
	}

	private void updateListFromDetails(int index) {
		if(index < 0 || index >= list.size())
			return;

		ExecutionData data = list.get(index);
		data.commandLineArguments = argumentsField.getText();
		listModel.elementChanged(index);
	}
}
