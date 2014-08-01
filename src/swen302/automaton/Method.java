package swen302.automaton;

import swen302.graph.Edge;

/**
 *Subclass to descirbe an method call transition
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class Method extends Edge {

	public Method(String longName, String shortName) {
		super(longName, shortName);
	}

}
