package swen302.automaton;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.tracer.Trace;

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
	public boolean processLine(String line) {

		if (isStateCall(line) ) { //Updates state of next node
			if (line.startsWith("staticContext")) {
				stack.push(null);
			}else{
				stack.push(getStateNode(line.substring(12)));
			}

		}
		else if(isReturn(line) && stack.size()>=1){ // Reads an instance of return call.

			Node finalState = stack.pop();
			Node initialState = stack.pop();

			if (finalState != null && initialState != null) {
				graph.addEdge(new Edge(String.valueOf(nodeCount++), AutomatonGraphUtils.formatMethodLabel(line.substring(7)), initialState, finalState));

				graph.nodes.add(finalState);
				graph.nodes.add(initialState);
			}

			return true;
		}

		return false;
	}


	/**
	 * Returns whether the line is a state call
	 * @param line
	 * @return
	 */
	private boolean isStateCall(String line) {
		return line.startsWith("staticContext") || line.startsWith("objectState");
	}


	/**
	 * Boolean to assert line is a return call
	 * @param line
	 * @return
	 */
	private boolean isReturn(String line) {
		return line.startsWith("return");
	}

	@Override
	public Graph generateGraph(Trace[] trace) {
		startIncremental();

		// can just concatenate multiple traces with this algorithm
		for(Trace t : trace)
			for(String l : t.lines)
				processLine(l);

		return getCurrentGraph();
	}

	@Override
	public String toString() {
		return "Simple field-based algorithm";
	}

}
