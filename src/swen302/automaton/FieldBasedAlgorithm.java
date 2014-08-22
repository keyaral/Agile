package swen302.automaton;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.tracer.Trace;
import swen302.tracer.TraceEntry;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class FieldBasedAlgorithm implements VisualizationAlgorithm, IncrementalVisualizationAlgorithm {

	private Graph graph;
	private Map<String, Node> states;
	private Stack<Node> stack;

	private int nodeCount;

	@Override
	public void startIncremental() {
		graph = new Graph();
		states = new HashMap<String, Node>();
		nodeCount = 0;
		stack = new Stack<Node>();
	}

	@Override
	public Graph getCurrentGraph() {
		return graph;
	}

	private Node getStateNode(String state) {
		if(!states.containsKey(state)){
			Node n = new Node(String.valueOf(nodeCount++));
			n.setState(state);
			states.put(state, n);
		}
		return states.get(state);
	}

	@Override
	public boolean processLine(TraceEntry line) {

		stack.push(line.state == null ? null : getStateNode(line.state));

		if(line.isReturn && stack.size()>=1){ // Reads an instance of return call.

			Node finalState = stack.pop();
			Node initialState = stack.pop();

			if (finalState != null && initialState != null) {
				graph.addEdge(new Edge(String.valueOf(nodeCount++), AutomatonGraphUtils.formatMethodLabel(line.getLongMethodName()), initialState, finalState));

				graph.nodes.add(finalState);
				graph.nodes.add(initialState);
			}

			return true;
		}

		return false;
	}


	@Override
	public Graph generateGraph(Trace[] trace) {
		startIncremental();

		// can just concatenate multiple traces with this algorithm
		for(Trace t : trace)
			for(TraceEntry l : t.lines)
				processLine(l);

		return getCurrentGraph();
	}

	@Override
	public String toString() {
		return "Simple field-based algorithm";
	}

}
