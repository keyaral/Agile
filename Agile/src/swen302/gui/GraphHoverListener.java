package swen302.gui;

import swen302.graph.Node;

public interface GraphHoverListener {

	/**
	 * Called when the mouse enters or leaves a node.
	 * @param node The node the mouse is over, or null if there is no such node.
	 */
	public void onMouseHover(Node node);
}
