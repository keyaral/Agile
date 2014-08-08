package swen302.gui.graphlayouts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.vertexgraph.Vector2D;

public class EadesSpringEmbedder {
	
	public Graph graph;
	public Graphics graphics;
	
	public double MAGNETIC_STRENGTH = 2.0;
	public double SPRING_STRENGTH = 2.0;
	public double FRICTION_STATIC = 0.3;
	public double FRICTION_KINETIC = 0.1;
	public double GRAVITY = -9.8;
	
	public EadesSpringEmbedder(Graph graph, int width, int height){
		this.graph = graph;
		
		System.out.println("Width: " + width + "  - Height: " + height);
		
		graph.generateInitialLayout(800, 600);
	}
	
	public void step(){
		//double totalEnergy = 0;
		
		for (Node n : graph.nodes) {
			Vector2D tempForce = new Vector2D(0.0, 0.0);
			
			for(Edge e : n.getConnections()) {
				tempForce.plus(hookesLaw(e.node1, e.node2));
			}
			
			for (Node m : graph.nodes) {
				if (n != m) {
					tempForce.plus(coulombsLaw(n, m));
				}
			}
			
			//tempForce.plus(friction(n));
			tempForce.plus(drag(n));
			n.velocity.plus(tempForce);
			//totalEnergy += n.kineticEnergy();
			
			//Stop calculating, will probably leave out.
			//This is so the graph can be made interactive
		}
	}
	
	private Vector2D drag(Node n) {
		return n.velocity
				.componentwiseProduct(new Vector2D(n.mass, n.mass))
				.componentwiseProduct(new Vector2D(0.25, 0.25));
	}
	
	private Vector2D friction(Node n) {
		if(n.velocity.length() > 1.0) {
			double friction = FRICTION_KINETIC * n.mass * GRAVITY;
			return new Vector2D(friction, friction);
		}
		else {
			double friction = FRICTION_STATIC * n.mass * GRAVITY;
			return new Vector2D(friction, friction);
		}
	}
	
	private Vector2D coulombsLaw(Node n1, Node n2) {
		double xDist = n1.position.getX() - n2.position.getX();
		double yDist = n1.position.getY() - n2.position.getY();
		
		double xComponent = MAGNETIC_STRENGTH * ((n1.REPULSION * n2.REPULSION) / xDist);
		double yComponent = MAGNETIC_STRENGTH * ((n1.REPULSION * n2.REPULSION) / yDist);
		
		return new Vector2D(xComponent, yComponent);
	}
	
	private Vector2D hookesLaw(Node n1, Node n2) {
		double xComponent = 0-(n1.position.x - n2.position.x);
		double yComponent = 0-(n1.position.y - n2.position.y);
		
		return new Vector2D(xComponent, yComponent);
	}
	
	public void draw(Graphics2D graphics) {
		
		graphics.setColor(Color.BLACK);
		
		for (Node n : graph.nodes) {
			
			for (Edge cn : n.getConnections())
			
				
			graphics.drawLine(
					(int)cn.node1.position.getX(),
					(int)cn.node1.position.getY(),
					(int)cn.node2.position.getX(),
					(int)cn.node2.position.getY()
			);
		}
		
		for (Node n : graph.nodes) {
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillOval((int)n.position.getX()-10, (int)n.position.getY()-10, 20, 20);
			graphics.setColor(Color.BLACK);
			graphics.drawOval((int)n.position.getX()-10, (int)n.position.getY()-10, 20, 20);
		}
	}

}
