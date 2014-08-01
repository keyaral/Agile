package swen302.automaton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class KTails {

	private List<List<Node>> traces = new ArrayList<List<Node>>();
	private String[] inputs = new String[]{"bcbca","aaabca","aabcbca","aaa"};

	private List<Edge[]> Edges = new ArrayList<Edge[]>();

	private List<Node> finalNodes = new ArrayList<Node>();

	private final int k = 3;

	public KTails(){
		for(String input: inputs){
			KTailsMain m = new KTailsMain(input);
			traces.add(m.getNodes());
		}
		for(List<Node> n : traces){
			printNodes(getOrderedArray(n));
		}
		createEdgeSets();
		new Graph().save(finalNodes);
	}

	private void createEdgeSets(){


		for(List<Node> n : traces){
			Edge[] prev = null;
			Edge[] t = getOrderedArray(n);
			for(int i=0; i<t.length-k; i++){
				Edge[] array = new Edge[k];
				for(int j=0; j<k; j++){
					array[j] = t[i+j];
				}
				////////////// k tail  array



				boolean contains = false;
				for(Edge[] trans : Edges ){
					if(Arrays.equals(trans, array)){
						contains = true;
						break;
					}
				}



				if(!contains){
					Edges.add(array);
				}
				connect(prev, array, !contains);
				prev = array;
			}
		}
	}

	private void connect(Edge[] prev, Edge[] next, boolean newNode){
		if(prev == null){ //New trace
			finalNodes.add(new Node(String.valueOf(Edges.size()-1)));
		}else if(newNode){
			Node newN = new Node(String.valueOf(Edges.size()-1));
			finalNodes.add(newN);
			Node prevNode = findNode(prev);
			prevNode.addNode(prev[0], newN);
		}else{
			//find a node that matches prev and connect with next that is found //both exist
			Node prevNode = findNode(prev);
			Node nextNode = findNode(next);
			prevNode.addNode(prev[0], nextNode);
		}
	}

	private Node findNode(Edge[] trans){
		for(int i=0; i<Edges.size(); i++ ){
			if(Arrays.equals(Edges.get(i), trans)){
				for(Node n : finalNodes){
					if(n.getID().equals(String.valueOf(i))){
						return n;
					}
				}
			}
		}
		return null;
	}

	private void printNodes(Edge[] trans){
		for(Edge t : trans){
			if(t != null){
				System.out.println(t.shortname);
			}
		}
	}

	private Edge[] getOrderedArray(List<Node> nodes){
		Edge[] toReturn = new Edge[nodes.size()];
		for(Node n : nodes){
			Map<Node,Edge> trans = n.getConnections();
			for(Node neigh : trans.keySet()){
				toReturn[Integer.parseInt(neigh.getID())-1] = trans.get(neigh);
			}
		}
		return toReturn;
	}



	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		new KTails();
	}

}
