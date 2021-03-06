package swen302.graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Node class holds the transitions calls between states.
 *
 * Nodes are numbered sequentially with the state appended
 * * @author Oliver Greenaway, Marian Clements
 *
 */
public class Node {
	private Set<Edge> outgoingEdges = new HashSet<Edge>();
	private Set<Edge> springs = new HashSet<Edge>();
	private String id;
	private Object label = "";
	private Vector2D position;
	private Vector2D velocity;
	private Vector2D acceleration;
	public Vector2D force;
	public double mass;
	public double REPULSION = 2.0;

	public boolean highlighted;

	public final boolean IsVirtual;

	/**
	 * Constructs a node with given label.
	 *
	 * @param id
	 */
	public Node(String id){
		this(new Vector2D(0.0, 0.0));
		this.id = id;
	}

	public Node(Vector2D position){
		this(position, false);

	}

	public Node(Vector2D position, boolean isVirtual){
		this.id = null;
		this.position = position;
		this.velocity = new Vector2D(0.0, 0.0);
		this.acceleration = new Vector2D(0.0, 0.0);
		this.force = new Vector2D(0.0, 0.0);
		this.IsVirtual = isVirtual;
	}

	public double getCharge() {
		return (this.outgoingEdges.size()+1)*0.00075;
	}

	public void updatePosition(double timestep){
		//F = m*a
		this.acceleration = this.force.scalarMultiply(mass);

		//Max Acceleration
		//if(this.acceleration.getNorm() > 1000)
		//	this.acceleration = this.acceleration.normalize().scalarMultiply(1000);

		//Velocity
		//Vf = Vi + a*t
		this.velocity = this.velocity.add(this.acceleration.scalarMultiply(timestep));

		//Displacement
		// d = v*t + 0.5*a*t^2
		double tsq = Math.pow(timestep, 2);
		Vector2D displacement1 = this.velocity.scalarMultiply(timestep);
		Vector2D displacement2 = this.acceleration.scalarMultiply(tsq);

		displacement2.scalarMultiply(0.5);

		Vector2D finalDisplacement = displacement1.add(displacement2);

		if (finalDisplacement.getNorm() > 0.5) {
			this.position = this.position.add(finalDisplacement);
		}
	}

	public Vector2D getVelocity(){return velocity; }
	public Vector2D getPosition(){ return position; }

	/**
	 * Adds connection from this nodes to another node specifying the transition between.
	 * @param trans
	 * @param n
	 */
	public void addOutgoingEdge(Edge trans){

		trans.duplicateCount = hasEdgeAlreadyCount(trans);

		outgoingEdges.add(trans);
		addSpring(trans);
		trans.getOtherNode(this).addSpring(trans);
	}

	private int hasEdgeAlreadyCount(Edge tran) {
		int count = 0;

		for (Edge e : outgoingEdges)
			if (e.node2.equals(tran.node2))
				count++;

		return count;
	}

	public void addSpring(Edge edge){
		springs.add(edge);
	}


	/**
	 * Returns the lists of connection from this node to other nodes
	 * @return
	 */
	public Set<Edge> getConnections(){
		return outgoingEdges;
	}

	public Set<Edge> getSprings(){
		return springs;
	}

	public void setPosition(Vector2D newPos) {
		this.position = newPos;
	}

	public void setLabel(Object label) {
		this.label = label;
	}

	/**
	 * Returns the ID of the node with the state appended if one exists.
	 * @return
	 */
	public String getFormattedLabel(){
		if(!LabelFormatOptions.displayState) {
			if(LabelFormatOptions.displayID)
				return id;
			else
				return "";

		} else {
			if(LabelFormatOptions.displayID)
				return id+": "+label;
			else
				return String.valueOf(label);
		}
	}

	/**
	 * Returns the ID of the node.
	 * @return
	 */
	public String getID(){
		return id;
	}

	public void randPosition(Random rand) {
		int x = (int)Math.floor(rand.nextDouble() * 600);//TODO remove hard coded var
		int y = (int)Math.floor(rand.nextDouble() * 600);
		this.position = new Vector2D(x,y);

	}

	public void setPosition(int x, int y){
		this.position = new Vector2D(x,y);
	}

	public Object getLabel() {
		return label;
	}
}
