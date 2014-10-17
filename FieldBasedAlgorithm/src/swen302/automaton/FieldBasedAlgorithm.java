package swen302.automaton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.tracer.Trace;
import swen302.tracer.TraceEntry;
import swen302.tracer.state.State;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class FieldBasedAlgorithm implements VisualizationAlgorithm, IncrementalVisualizationAlgorithm {

	private Graph graph;
	private Map<State, Node> states;
	private Stack<Node> stack;
	private Set<String> addedEdges = new HashSet<String>();
	private Stack<TraceEntry> callEntries;

	private int nodeCount;

	@Override
	public void startIncremental() {
		graph = new Graph();
		states = new HashMap<State, Node>();
		nodeCount = 0;
		stack = new Stack<Node>();
		callEntries = new Stack<TraceEntry>();
	}

	@Override
	public Graph[] getCurrentGraphs() {
		return new Graph[] {graph};
	}

	private Node getStateNode(State state) {
		if(!states.containsKey(state)){
			Node n = new Node(String.valueOf(nodeCount++));
			n.setLabel(state);
			states.put(state, n);
		}
		return states.get(state);
	}

	@Override
	public boolean processLine(TraceEntry line) {

		stack.push(line.state == null ? null : getStateNode(line.state));

		if(!line.isReturn)
			callEntries.push(line);

		if(line.isReturn && stack.size()>=1){ // Reads an instance of return call.

			Node finalState = stack.pop();
			Node initialState = stack.pop();

			TraceEntry callEntry = callEntries.pop();

			List<State> arguments = callEntry.arguments;

			if (finalState != null && initialState != null) {
				String edgeID = initialState.getID()+" "+finalState.getID()+" "+line.getLongMethodName()+" "+callEntry.arguments;
				if(addedEdges.add(edgeID)) { // don't add duplicate edges

					graph.addEdge(new Edge(String.valueOf(nodeCount++), AutomatonGraphUtils.createMethodLabelObject(line.getLongMethodName(), arguments), initialState, finalState));

					if(!graph.nodes.contains(finalState)) graph.addNode(finalState);
					if(!graph.nodes.contains(initialState)) graph.addNode(initialState);
				}
			}

			return true;
		}

		return false;
	}


	@Override
	public Graph[] generateGraph(Trace[] trace) {
		startIncremental();

		// can just concatenate multiple traces with this algorithm
		for(Trace t : trace)
			for(TraceEntry l : t.lines)
				processLine(l);

		return getCurrentGraphs();
	}

	@Override
	public String toString() {
		return "Simple field-based algorithm";
	}

}
