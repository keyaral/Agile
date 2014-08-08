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
import swen302.vertexgraph.Vertex;
import swen302.vertexgraph.VertexGraph;

public class EadesSpringEmbedder {
	
	public VertexGraph graph;
	public Graphics graphics;
	
	public double GLOBALC_STRENGTH = 2.0;
	
	public EadesSpringEmbedder(VertexGraph graph, int width, int height){
		this.graph = graph;
		
		graph.generateInitialLayout(width, height);
	}
	
//	public void step(){
//		double totalEnergy = 0;
//		
//		Set<Node> connected = new HashSet<Node>();
//		
//		for(Vertex v:graph.vertices){ connected.addAll(v.getConnections().keySet()); }
//		
//		for (Vertex v : graph.vertices) {
//			Vector2D tempForce = new Vector2D(0.0, 0.0);
//			
//			for(Node cNode : connected) {
//				
//				Vertex cv;
//				
//				for (Vertex ev : graph.vertices) {
//					if (v.getID().equals(ev.getID())){ cv = ev; }
//				}
//				
//				tempForce.plus(hookesLaw(v, cv));
//			}
//			
//			for (Vertex w : graph.verticies) {
//				if (v != w) {
//					tempForce.plus(coulombsLaw(v, w));
//				}
//			}
//			
//			tempForce.plus(friction(v));
//			tempForce.plus(drag(v));
//			v.velocity.plus(tempForce);
//			totalEnergy += v.kineticEnergy();
//			
//			//Stop calculating, will probably leave out.
//			//This is so the graph can be made interactive
//		}
//	}
//	
//	private Vector2D drag(Vertex v) {
//		
//	}
//	
//	private Vector2D friction(Vertex v) {
//		
//	}
//	
//	private Vector2D coulombsLaw(Vertex v1, Vertex v2) {
//		double xDist = v1.position.getX() - v2.position.getX();
//		double yDist = v1.position.getY() - v2.position.getY();
//		
//		double xComponent = GLOBALC_STRENGTH * ((v1.REPULSION * v2.REPULSION) / xDist);
//		double yComponent = GLOBALC_STRENGTH * ((v1.REPULSION * v2.REPULSION) / yDist);
//		
//		return new Vector2D(xComponent, yComponent);
//	}
//	
//	private Vector2D hookesLaw(Vertex v1, Vertex v2) {
//		
//	}
	
	public void draw(Graphics2D graphics) {
		
		graphics.setColor(Color.BLACK);
		
		System.out.println("nVertices" + graph.vertices.size());
		
		for (Vertex v : graph.vertices) {
			
			System.out.println("Graph Vertex size" + v.getID());
			
			Set<Node> connected = new HashSet<Node>();
			connected = v.getConnections().keySet();
			
			System.out.println(connected.size());
			
			for(Node cNode : connected) {
				String searchLabel = cNode.getLabel();
				for(Vertex gv : graph.vertices) {
					
					System.out.println(searchLabel + " - " + gv.getLabel());
					
					if(searchLabel.equals(gv.getLabel())) {
						graphics.drawLine(
								(int)v.position.getX(),
								(int)v.position.getY(),
								(int)gv.position.getX(),
								(int)gv.position.getY()
						);
						System.out.println((int)v.position.getX() + " - " +
								(int)v.position.getY() + " - " +
								(int)gv.position.getX() + " - " +
								(int)gv.position.getY());
					}
				}
			}
		}
		
		for (Vertex v : graph.vertices) {
			graphics.drawOval((int)v.position.getX(), (int)v.position.getY(), 20, 20);
		}
	}

}
