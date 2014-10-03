package swen302.automaton;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.graph.PetriTransitionNode;
import swen302.tracer.Trace;

public class PetriNetAlgorithm implements VisualizationAlgorithm {

	@Override
	public Graph generateGraph(Trace[] trace) {
		Node a = new Node("a");
		Node b = new Node("b");
		Node c = new Node("c");
		Node d = new Node("d");
		PetriTransitionNode t1 = new PetriTransitionNode("t1");
		PetriTransitionNode t2 = new PetriTransitionNode("t2");
		a.setState("a");
		b.setState("b");
		c.setState("c");
		d.setState("d");
		t1.setState("t1");
		t2.setState("t2");

		Graph g = new Graph();
		g.addNode(a); g.addNode(b); g.addNode(c); g.addNode(d); g.addNode(t1); g.addNode(t2);
		g.addEdge(new Edge("e1", "e1", a, t1));
		g.addEdge(new Edge("e2", "e2", b, t1));
		g.addEdge(new Edge("e3", "e3", t1, c));
		g.addEdge(new Edge("e4", "e4", c, t2));
		g.addEdge(new Edge("e5", "e5", t2, d));
		g.addEdge(new Edge("e6", "e6", d, t2));
		return g;
	}

	@Override
	public String toString() {
		return "Petri Net Algorithm";
	}

}

