package swen302.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class MultiSplitPane extends JPanel {
	private static final long serialVersionUID = 1L;

	public MultiSplitPane() {
		setLayout(new BorderLayout());
	}

	private JComponent[] contents;

	public void setContents(JComponent[] contents) {

		this.contents = contents;

		this.removeAll();

		if(contents.length == 0)
			return;

		//setBorder(BorderFactory.createLineBorder(Color.red));

		// Make a chain of nested splitters.
		// The right side of each splitter is either the final element, or another splitter.

		Container nextParent = this;

		for(int k = 0; k < contents.length - 1; k++) {
			JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			//splitter.setBorder(BorderFactory.createLineBorder(Color.red));
			splitter.setBorder(null);
			nextParent.add(splitter, (nextParent == this ? BorderLayout.CENTER : null));
			splitter.add(contents[k]);
			nextParent = splitter;
		}

		nextParent.add(contents[contents.length-1]);

		revalidate();

	}

	public JComponent[] getContents() {
		return contents;
	}

}
