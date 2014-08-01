package swen302.automaton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class KTails {

	private List<List<Node>> traces = new ArrayList<List<Node>>();
	private String[] inputs = new String[]{"bcbca","aaabca","aabcbca","aaa"};

	private List<Transition[]> transitions = new ArrayList<Transition[]>();

	private List<Node> finalNodes = new ArrayList<Node>();

	private final int k = 3;

	public KTails(){
		for(String input: inputs){
			Main m = new Main(input);
			traces.add(m.getNodes());
		}
		for(List<Node> n : traces){
			printNodes(getOrderedArray(n));
		}
		createTransitionSets();
		new Graph().save(finalNodes);
	}

	private void createTransitionSets(){


		for(List<Node> n : traces){
			Transition[] prev = null;
			Transition[] t = getOrderedArray(n);
			for(int i=0; i<t.length-k; i++){
				Transition[] array = new Transition[k];
				for(int j=0; j<k; j++){
					array[j] = t[i+j];
				}
				////////////// k tail  array



				boolean contains = false;
				for(Transition[] trans : transitions ){
					if(Arrays.equals(trans, array)){
						contains = true;
						break;
					}
				}



				if(!contains){
					transitions.add(array);
				}
				connect(prev, array, !contains);
				prev = array;
			}
		}
	}

	private void connect(Transition[] prev, Transition[] next, boolean newNode){
		if(prev == null){ //New trace
			finalNodes.add(new Node(String.valueOf(transitions.size()-1)));
		}else if(newNode){
			Node newN = new Node(String.valueOf(transitions.size()-1));
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

	private Node findNode(Transition[] trans){
		for(int i=0; i<transitions.size(); i++ ){
			if(Arrays.equals(transitions.get(i), trans)){
				for(Node n : finalNodes){
					if(n.getID().equals(String.valueOf(i))){
						return n;
					}
				}
			}
		}
		return null;
	}

	private void printNodes(Transition[] trans){
		for(Transition t : trans){
			if(t != null){
				System.out.println(t.shortname);
			}
		}
	}

	private Transition[] getOrderedArray(List<Node> nodes){
		Transition[] toReturn = new Transition[nodes.size()];
		for(Node n : nodes){
			Map<Node,Transition> trans = n.getConnections();
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
