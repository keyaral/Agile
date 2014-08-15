package swen302.automaton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.tracer.Trace;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class KTailsAlgorithm implements VisualizationAlgorithm {

	private List<Trace> traces = new ArrayList<>();
	private List<String[]> edges = new ArrayList<>();
	private Graph finalGraph = new Graph();
	private int nextEdgeID = 0;

	public static int k = 3;



	/**
	 * Takes traces and creates connections between each Node ensuring nodes with equal values are not duplicated
	 */
	private void createEdgeSets(){

		// Goes through a trace and orders them in the order the methods were called

		for(Trace n : traces){
			String[] prev = null;
			List<String> calls = new ArrayList<>();
			for(String line : n.lines)
				if(line.startsWith("methodCall "))
					calls.add(line.substring(11));
			String[] t = calls.toArray(new String[calls.size()]);

			// Then splits up the trace into k sized array of the method calls.
			//(each array is different by removing the first and adding the next method from the trace)
			for(int i=0; i<t.length-k; i++){
				String[] array = new String[k];
				for(int j=0; j<k; j++){
					array[j] = t[i+j];
				}

				// Check that that set of of method calls in the array doesn't already exist in the set of Nodes
				boolean contains = false;
				for(String[] trans : edges ){
					if(Arrays.equals(trans, array)) {
						contains = true;
						break;
					}
				}

// if it isn't contained already then add it to the list of edges to be recorded

				if(!contains){
					edges.add(array);
				}


	// Calls connect using that array, a previous array if defined and the boolean of if it already exists.
				connect(prev, array, !contains);
				prev = array; // Makes the current array previous to be reference by other arrays in the trace.
			}
		}
	}

	/**
	 * Connects nodes to form the K-Tail automaton
	 * @param prev	An array of the previous method call followed by the following k-1 calls in the trace
	 * @param next	An array of the current method call followed by k-1 calls in the trace
	 * @param newNode	Boolean defining if the current node does not already exist
	 */
	private void connect(String[] prev, String[] next, boolean newNode){

		// Node already exist, and is the first array in the trace
		if(prev==null && !newNode)return;

		if(prev == null){ //New trace, and new original node
			Node n = new Node(String.valueOf(finalGraph.nodes.size()));
			n.setKState(getMethodStateString(next));
			finalGraph.nodes.add(n);

		}else if(newNode){ // new node within a trace
			Node newN = new Node(String.valueOf(finalGraph.nodes.size()));
			newN.setKState(getMethodStateString(next));
			finalGraph.nodes.add(newN);
			Node prevNode = findNode(prev);
			finalGraph.addEdge(new Edge(String.valueOf(nextEdgeID++), prev[0], prevNode, newN));
		}else{
			//find a node that matches prev and connect with next that is found //both exist
			Node prevNode = findNode(prev);
			Node nextNode = findNode(next);
			finalGraph.addEdge(new Edge(String.valueOf(nextEdgeID++), prev[0], prevNode, nextNode));
		}
	}
// Returns a string of the array of method calls
	private String getMethodStateString(String[] edges){
		String toReturn = "";
		for(String e : edges){
			if(e != null){
				toReturn += AutomatonGraphUtils.formatMethodLabel(e)+",";
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
		// iterate through nodes and returns a node if the states match.
		for(Node node : finalGraph.nodes) {
			if(node.getKState().equals(getMethodStateString(trans))){
				return node;
			}
		}

		return null;
	}


	@Override
	public Graph generateGraph(Trace[] traces) {
		this.traces.addAll(Arrays.asList(traces));
		createEdgeSets();
		return finalGraph;
	}


	@Override
	public String toString() {
		return "K-Tails algorithm";
	}

}
