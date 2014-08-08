package swen302.automaton;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	private List<List<Node>> traces = new ArrayList<List<Node>>();
	private String[] inputs = new String[]{"bcbca","aaabca","aabcbca","aaa","dcd","dcba","abcdabcd","efabc","fffffffffff","ccbbee","abcdef","cbafff"};

	private List<Edge[]> Edges = new ArrayList<Edge[]>();

	private Graph finalGraph = new Graph();

	public static int k = 3;

	/**
	 * Constructs a new KTail algorithm, runs multiple traces using inputs as parameters
	 * Creates a Graph out of traces and saves as a GraphSaver image.
	 */
//	public KTails(){
//		for(String input: inputs){
//			KTailsMain m = new KTailsMain(input);
//			traces.add(m.getNodes());
//		}
//		createEdgeSets();
//		try {
//			GraphSaver.save(finalGraph,new File("output.png"));
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Takes traces and creates connections between each Node ensuring nodes with equal values are not duplicated
	 */
	private void createEdgeSets(){

		// Goes through a trace and orders them in the order the methods were called

		for(List<Node> n : traces){
			Edge[] prev = null;
			Edge[] t = getOrderedArray(n);

			// Then splits up the trace into k sized array of the method calls.
			//(each array is different by removing the first and adding the next method from the trace)
			for(int i=0; i<t.length-k; i++){
				Edge[] array = new Edge[k];
				for(int j=0; j<k; j++){
					array[j] = t[i+j];
				}

				// Check that that set of of method calls in the array doesn't already exist in the set of Nodes
				boolean contains = false;
				for(Edge[] trans : Edges ){
					if(equalNames(trans, array)){
						contains = true;
						break;
					}
				}

// if it isn't contained already then add it to the list of edges to be recorded

				if(!contains){
					Edges.add(array);
				}


	// Calls connect using that array, a previous array if defined and the boolean of if it already exists.
				connect(prev, array, !contains);
				prev = array; // Makes the current array previous to be reference by other arrays in the trace.
			}
		}
	}

	private boolean equalNames(Edge[] trans, Edge[] array) {
		for(int k = 0; k < trans.length; k++)
			if(!trans[k].longname.equals(array[k].longname))
				return false;
		return true;
	}

	/**
	 * Connects nodes to form the K-Tail automaton
	 * @param prev	An array of the previous method call followed by the following k-1 calls in the trace
	 * @param next	An array of the current method call followed by k-1 calls in the trace
	 * @param newNode	Boolean defining if the current node does not already exist
	 */
	private void connect(Edge[] prev, Edge[] next, boolean newNode){

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
			finalGraph.addEdge(new Edge(prev[0], prevNode, newN));
		}else{
			//find a node that matches prev and connect with next that is found //both exist
			Node prevNode = findNode(prev);
			Node nextNode = findNode(next);
			finalGraph.addEdge(new Edge(prev[0], prevNode, nextNode));
		}
	}
// Returns a string of the array of method calls
	private String getMethodStateString(Edge[] edges){
		String toReturn = "";
		for(Edge e : edges){
			if(e != null){
				toReturn += e.shortname+",";
			}
		}
		return toReturn;
	}

	/**
	 * Returns the node that contains identical method calls to the given array
	 * @param trans	Array of method calls
	 * @return Node if one is found that matches, else returns null
	 */
	private Node findNode(Edge[] trans){
		/*for(int i=0; i<Edges.size(); i++ ){
			if(Arrays.equals(Edges.get(i), trans)){
				for(Node n : finalNodes){
					if(n.getID().equals(String.valueOf(i))){
						return n;
					}
				}
			}
		}*/
		// iterate through nodes and returns a node if the states match.
		for(Node node : finalGraph.nodes) {
			if(node.getKState().equals(getMethodStateString(trans))){
				return node;
			}
		}

		return null;
	}

// Print all nodes as thier short names
	private void printNodes(Edge[] trans){
		for(Edge t : trans){
			if(t != null){
				System.out.println(t.shortname);
			}
		}
	}

	/**
	 * Returns an arraylist of method calls in the order that they were called in the trace.
	 * @param nodes
	 * @return
	 */
	private Edge[] getOrderedArray(List<Node> nodes){
		Edge[] toReturn = new Edge[nodes.size()];
		for(Node n : nodes){
			Set<Edge> trans = n.getConnections();
			for(Edge edge : trans){
				toReturn[Integer.parseInt(edge.getOtherNode(n).getID())-1] = edge;
			}
		}
		return toReturn;
	}



	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		new KTailsAlgorithm();
	}

	@Override
	public Graph generateGraph(Trace[] trace) {
		for(Trace input: trace){
			KTailsProccessing m = new KTailsProccessing(input);
			traces.add(m.getNodes());
		}
		createEdgeSets();
		return finalGraph;
	}


	@Override
	public String toString() {
		return "K-Tails algorithm";
	}

}
