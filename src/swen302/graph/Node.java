package swen302.graph;

import java.awt.geom.Point2D;
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
	
	private Set<Edge> outgoingEdges = new HashSet<>();
	private String id;
	private String state = "";
	private String Ktailstate = ""; //A string for recording Ktail strings in Graph using Ktails
	private Vector2D position;
	private Vector2D velocity;
	private Vector2D acceleration;
	public Vector2D force;
	public double mass;
	public double REPULSION = 2.0;
	
	public static final double uStatic = 10;//1.0;
	public static final double uKinetic = 5;//0.8;
	public static final double gravity = -9.8;
	
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
		
		System.out.println("One -> " + this.force + " - " + friction);
		
		//Friction force should oppose the force
		Vector2D frictionForce = new Vector2D(0.0, 0.0);
		if (this.force.getNorm() > 0) {
			frictionForce = this.force.normalize().scalarMultiply(friction).negate();
		}
		if (this.force.getNorm() < friction) {
			//Make it so that it doesn't move if the force isn't strong enough to overcome friction
			frictionForce = this.force.normalize().scalarMultiply(this.force.getNorm()).negate();
		}
		
		this.force = this.force.subtract(frictionForce);
		
		System.out.println("One -> " + this.force + " - " + frictionForce);
		
		//F = m*a
		this.acceleration = this.force.scalarMultiply(mass);
		
		//Max Acceleration
		if(this.acceleration.getNorm() > 100)
			this.acceleration = this.acceleration.normalize().scalarMultiply(100);
		
		//Displacement
		// d = v*t + 0.5*a*t^2
		double tsq = Math.pow(timestep, 2);
		Vector2D displacement1 = this.velocity.scalarMultiply(timestep);
		Vector2D displacement2 = this.acceleration.scalarMultiply(tsq);
		
		displacement2.scalarMultiply(0.5);
		
		Vector2D finalDisplacement = displacement1.add(displacement2);
		
		this.position = this.position.add(finalDisplacement);
	}
	
	public Vector2D getVelocity(){ return velocity; }
	public Vector2D getPosition(){ return position; }
	
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
}
