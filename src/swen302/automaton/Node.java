package swen302.automaton;
import java.util.HashMap;
import java.util.Map;


public class Node {
	private Map<Node,Transition> connections = new HashMap<Node,Transition>();
	private String label;

	public Node(String label){
		this.label = label;
	}

	public void addNode(Transition trans, Node n){
		connections.put(n, trans);
	}

	public Map<Node,Transition> getConnections(){
		return connections;
	}

	public String getLabel(){
		return label;
	}
}
