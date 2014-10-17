package swen302.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

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

		this.setLayout(new GridLayout(1, contents.length*2-1, 5, 0));

		for(JComponent jc : contents)
			this.add(jc);

		revalidate();

	}

	public JComponent[] getContents() {
		return contents;
	}

}
