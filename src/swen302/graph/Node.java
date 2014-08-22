package swen302.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
	private String state = "";
	private String Ktailstate = ""; //A string for recording Ktail strings in Graph using Ktails
	private Vector2D position;
	private Vector2D velocity;
	private Vector2D acceleration;
	public Vector2D force;
	public double mass;
	public double REPULSION = 2.0;

	public static final double uStatic = 0.8;//1.0;
	public static final double uKinetic = 0.4;//0.8;
	public static final double gravity = -9.8;

	public Rectangle2D labelBounds;

	public final boolean IsVirtual;

	/**
	 * Constructs a node with given label.
	 *
	 * @param id
	 */
	public Node(String id){
		this.id = id;
		this.position = new Vector2D(0.0, 0.0);
		this.velocity = new Vector2D(0.0, 0.0);
		this.acceleration = new Vector2D(0.0, 0.0);
		this.force = new Vector2D(0.0, 0.0);
		IsVirtual = false;
	}

	public Node(Vector2D position){
		this.id = null;
		this.position = position;
		this.velocity = new Vector2D(0.0, 0.0);
		this.acceleration = new Vector2D(0.0, 0.0);
		this.force = new Vector2D(0.0, 0.0);
		IsVirtual = false;
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
		return (this.outgoingEdges.size()+1)*0.00055;
	}

	public void updatePosition(double timestep){
		//Apply friction
		double friction;

		if(this.velocity.getNorm() < 0.1) {
			friction = uStatic*mass*gravity;
		}
		else {
			friction = uKinetic*mass*gravity;
		}

		//Friction force should oppose the force
		Vector2D frictionForce = new Vector2D(0.0, 0.0);
		if (this.force.getNorm() > 0) {
			frictionForce = this.force.normalize().scalarMultiply(friction).negate();
		}
		if (this.force.getNorm() < friction) {
			//Make it so that it doesn't move if the force isn't strong enough to overcome friction
			frictionForce = this.force;
		}

		this.force = this.force.add(frictionForce);

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
		outgoingEdges.add(trans);
		addSpring(trans);
		trans.getOtherNode(this).addSpring(trans);
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

	public void setPosition(Vector2D newPos) {
		this.position = newPos;
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
		return (id + (state.equals("")?Ktailstate.equals("")?"":": "+Ktailstate:": "+ state));
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

	public void randPosition(Random rand) {
		int x = (int)Math.floor(rand.nextDouble() * 600);
		int y = (int)Math.floor(rand.nextDouble() * 600);
		this.position = new Vector2D(x,y);

	}

	public void setLabel(Rectangle2D stringBounds) {
		this.labelBounds = stringBounds;
	}
}
