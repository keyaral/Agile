package swen302.automaton;

import swen302.graph.Edge;
import swen302.graph.Node;

/**
 *Subclass to describe an return call transition
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class Return extends Edge {

	public Return(String longName, String shortName, Node node1, Node node2) {
		super(longName, node1, node2);
	}

}
