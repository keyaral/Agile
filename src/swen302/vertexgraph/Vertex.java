package swen302.vertexgraph;

import java.awt.geom.Point2D;
import java.util.List;

import swen302.graph.Node;

public class Vertex extends Node{
	public Point2D position;
	public Vector2D velocity;
	public double mass;
	public double REPULSION = 2.0;
	
	public Vertex(String id, String label) {
		super(id);
		super.state = label;
		
		position = new Point2D.Double(0.0, 0.0);
		velocity = new Vector2D(0.0, 0.0);
	}

	public double kineticEnergy() {
		return 0.5*(mass*(velocity.dotProduct(velocity)));
	}
}
