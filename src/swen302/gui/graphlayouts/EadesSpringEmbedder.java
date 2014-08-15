package swen302.gui.graphlayouts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;

public class EadesSpringEmbedder {
	
	public Graph graph;
	public Graphics graphics;
	
	public double MAGNETIC_STRENGTH = 200000.0;
	public double SPRING_STRENGTH = -20.0;
	public double FRICTION_STATIC = 0.3;
	public double FRICTION_KINETIC = 0.1;
	public double GRAVITY = -0.98; // =) Jst liek IRL! =D
	
	public EadesSpringEmbedder(Graph graph, int width, int height){
		this.graph = graph;
		
		graph.generateInitialLayout(800, 600);
	}
	
	public void step(double timeStep){
		//double totalEnergy = 0;
		
		for (Node n : graph.nodes) {
			Vector2D tempForce = new Vector2D(0.0, 0.0);
			
			for (Edge e : n.getConnections()) {
				Vector2D hl = hookesLaw(n, e.getOtherNode(n));
				tempForce = tempForce.add(hl);
			}
			
			for (Node m : graph.nodes) {
				if (n != m) {
					tempForce = tempForce.add(coulombsLaw(n, m));
				}
			}
			
			//Pull towards center
			//TODO: Actual center
			Vector2D centerGravity = new Vector2D(300, 400);
			
			double distance = n.getPosition().distance(centerGravity);
			double force = distance;
			
			Vector2D vecResult = n.getPosition().subtract(centerGravity);
			vecResult = vecResult.normalize();
			vecResult = vecResult.scalarMultiply(force);
			vecResult = vecResult.negate();
			
			
			
			tempForce = tempForce.add(vecResult);
			tempForce = tempForce.subtract(drag(n.getVelocity()));
			
			n.force = tempForce;
		}
		for (Node n : graph.nodes) {
			n.updatePosition(timeStep);
			//Stop calculating, will probably leave out.
			//This is so the graph can be made interactive
		}
	}
	
	/*
	 * Needs to be redone / thought about in terms of vectors
	 * 
	 */
	private Vector2D coulombsLaw(Node n1, Node n2) {
		double distance = n1.getPosition().distance(n2.getPosition());
		
		double force = (this.MAGNETIC_STRENGTH*1*1)/(Math.pow(distance, 2));
		
		Vector2D vecResult = n1.getPosition().subtract(n2.getPosition());
		vecResult = vecResult.normalize();
			
		vecResult = vecResult.scalarMultiply(force);
		
		return vecResult;
	}
	
	private Vector2D drag(Vector2D velocity) {
		double force = 0.25*1*velocity.getNorm();
		return velocity.scalarMultiply(force);
	}
	
	private Vector2D hookesLaw(Node n1, Node n2) {
		// F = -K(x-N)
		
		double length = 40.0;
		
		Vector2D vecResult = n1.getPosition().subtract(n2.getPosition()); //The vector between the two nodes
		Vector2D springLength = vecResult.normalize();
		
		springLength = springLength.scalarMultiply(length);
		vecResult = vecResult.subtract(springLength);
		vecResult = vecResult.scalarMultiply(SPRING_STRENGTH);
		
		return vecResult;
		
	}
	
	public void draw(Graphics2D graphics) {
		
		graphics.setColor(Color.BLACK);
		
		for (Node n : graph.nodes) {
			
			for (Edge cn : n.getConnections())
			
				
			graphics.drawLine(
					(int)cn.node1.getPosition().getX(),
					(int)cn.node1.getPosition().getY(),
					(int)cn.node2.getPosition().getX(),
					(int)cn.node2.getPosition().getY()
			);
		}
		
		for (Node n : graph.nodes) {
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillOval((int)n.getPosition().getX()-10, (int)n.getPosition().getY()-10, 20, 20);
			graphics.setColor(Color.BLACK);
			graphics.drawOval((int)n.getPosition().getX()-10, (int)n.getPosition().getY()-10, 20, 20);
		}
	}

}
