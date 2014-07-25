package swen302.automaton;
import java.util.HashMap;
import java.util.Map;

/**
 * Node class holds the transitions calls between states.
 *
 * Nodes are numbered sequentially
 * * @author Oliver Greenaway, Marian Clements
 *
 */
public class Node {
	private Map<Node,Transition> connections = new HashMap<Node,Transition>();
	private String label;
	private String state = "";


	/**
	 * Constructs a node with given label.
	 *
	 * @param label
	 */
	public Node(String label){
		this.label = label;
	}

	/**
	 * Adds connection from this nodes to another node specifying the transition between.
	 * @param trans
	 * @param n
	 */
	public void addNode(Transition trans, Node n){
		connections.put(n, trans);
	}


	/**
	 * Returns the lists of connection from this node to other nodes
	 * @return
	 */
	public Map<Node,Transition> getConnections(){
		return connections;
	}


	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Returns the name of the node.
	 * @return
	 */
	public String getLabel(){
		return (label + (state.equals("")?"":": "+ state));
	}

	public String getID(){
		return label;
	}
}
