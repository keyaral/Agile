package swen302.graph;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import swen302.vertexgraph.Vector2D;

/**
 * Node class holds the transitions calls between states.
 *
 * Nodes are numbered sequentially with the state appended
 * * @author Oliver Greenaway, Marian Clements
 *
 */
public class Node {

	private Set<Edge> outgoingEdges = new HashSet<>();
	private String id;
	private String state = "";
	private String Ktailstate = ""; //A string for recording Ktail strings in Graph using Ktails
	public Point2D.Double position;
	public Vector2D velocity;
	public double mass;
	public double REPULSION = 2.0;

	/**
	 * Constructs a node with given label.
	 *
	 * @param id
	 */
	public Node(String id){
		this.id = id;
		this.position = new Point2D.Double(0.0, 0.0);
		this.velocity = new Vector2D(0.0, 0.0);
	}

	/**
	 * Adds connection from this nodes to another node specifying the transition between.
	 * @param trans
	 * @param n
	 */
	public void addOutgoingEdge(Edge trans){
		outgoingEdges.add(trans);
	}


	/**
	 * Returns the lists of connection from this node to other nodes
	 * @return
	 */
	public Set<Edge> getConnections(){
		return outgoingEdges;
	}

	/**
	 * Returns the String stored in state
	 * @return
	 */
	public String getState(){
		return state;
	}

	public String getKState(){
		return Ktailstate;
	}


	/**
	 * Sets the state of the node
	 * @param state
	 */
	public void setState(String state) {
		this.state = state;
	}
	public void setKState(String state) {
		this.Ktailstate = state;
	}

	/**
	 * Returns the ID of the node with the state appended if one exists.
	 * @return
	 */
	public String getLabel(){
		return ((GraphSaver.displayID?id:"") + (GraphSaver.displayState?(state.equals("")?Ktailstate.equals("")?"":(GraphSaver.displayID?": ":"")+Ktailstate:(GraphSaver.displayID?": ":"")+ state):""));
	}

	/**
	 * Returns the ID of the node.
	 * @return
	 */
	public String getID(){
		return id;
	}

	public double kineticEnergy() {
		return 0.5*(mass*(velocity.dotProduct(velocity)));
	}
}
