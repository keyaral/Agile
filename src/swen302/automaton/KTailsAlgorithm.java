package swen302.automaton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class KTailsAlgorithm implements VisualizationAlgorithm, IncrementalVisualizationAlgorithm {

	private List<Trace> traces = new ArrayList<>();
	private Graph finalGraph = new Graph();
	private int nextEdgeID = 0;
	private Map<String, Node> nodes = new HashMap<>();

	public static int k = 3;


	private String[] prev;

	private void startTrace() {
		prev = new String[k];
	}

	@Override
	public Graph getCurrentGraph() {
		return finalGraph;
	}

	@Override
	public void startIncremental() {
		startTrace();
	}

	private void processCall(String methodIdentifier) {
		String[] old = Arrays.copyOf(prev, k);

		for(int i = 1; i < k; i++)
			prev[i-1] = prev[i];
		prev[k-1] = methodIdentifier;
		if(prev[0] == null)
			return;

		connect(old[0] == null ? null : old, prev);
	}


	@Override
	public boolean processLine(TraceEntry line) {
		if(!line.isReturn) {
			processCall(line.getLongMethodName());
			return true;
		}
		return false;
	}

	/**
	 * Takes traces and creates connections between each Node ensuring nodes with equal values are not duplicated
	 */
	private void createEdgeSets(){
		for(Trace n : traces){
			startTrace();
			for(TraceEntry line : n.lines)
				processLine(line);
		}
	}

	/**
	 * Connects the node corresponding to <var>prev</var> to the node corresponding to <var>next</var>.
	 * If <var>prev</var> is null, creates a new start node.
	 * @param prev	An array of the previous method call followed by the following k-1 calls in the trace
	 * @param next	An array of the current method call followed by k-1 calls in the trace
	 * @param newNode	Boolean defining if the current node does not already exist
	 */
	private void connect(String[] prev, String[] next){

		if(prev == null){ //New trace, and new original node
			Node n = new Node(String.valueOf(finalGraph.nodes.size()));
			nodes.put(getMethodStateString(next, false), n);
			n.setKState(getMethodStateString(next, true));
			finalGraph.addNode(n);

		} else {
			Node nextNode = findNode(next);
			Node prevNode = findNode(prev);

			if(nextNode == null) { // new node within a trace
				nextNode = new Node(String.valueOf(finalGraph.nodes.size()));
				nodes.put(getMethodStateString(next, false), nextNode);
				nextNode.setKState(getMethodStateString(next, true));
				finalGraph.addNode(nextNode);
			}

			finalGraph.addEdge(new Edge(String.valueOf(nextEdgeID++), AutomatonGraphUtils.formatMethodLabel(prev[0]), prevNode, nextNode));
		}
	}
// Returns a string of the array of method calls
	private String getMethodStateString(String[] edges, boolean formatted){
		String toReturn = "";
		for(String e : edges){
			if(e != null){
				toReturn += (formatted ? AutomatonGraphUtils.formatMethodLabel(e) : e)+",";
			}
		}
		if(toReturn.length() > 0){
			toReturn = toReturn.substring(0, toReturn.length()-1);
		}
		return toReturn;
	}

	/**
	 * Returns the node that contains identical method calls to the given array
	 * @param trans	Array of method calls
	 * @return Node if one is found that matches, else returns null
	 */
	private Node findNode(String[] trans){
		return nodes.get(getMethodStateString(trans, false));
	}


	@Override
	public Graph generateGraph(Trace[] traces) {
		this.traces.addAll(Arrays.asList(traces));
		createEdgeSets();
		return finalGraph;
	}


	@Override
	public String toString() {
		return "K-Tails";
	}

}
