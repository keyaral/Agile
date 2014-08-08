package swen302.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Graph {
	public Set<Node> nodes = new HashSet<Node>();
	public Set<Edge> edges = new HashSet<Edge>();

	public static Graph createFromNodes(Collection<? extends Node> nodes) {
		Set<Edge> edges = new HashSet<Edge>();
		for(Node n : nodes)
			edges.addAll(n.getConnections());

		Graph g = new Graph();
		g.nodes.addAll(nodes);
		g.edges.addAll(edges);
		return g;
	}


	public void addEdge(Edge edge) {
		edges.add(edge);
		edge.node1.addOutgoingEdge(edge);
	}
	
	public void generateInitialLayout(int width, int height) {
		Random rand = new Random();
		
		for (Node n : nodes) {
			int x = (int)Math.floor(rand.nextDouble()*width);
			int y = (int)Math.floor(rand.nextDouble()*height);
			
			System.out.println(x + " - " + y);
			n.position.setLocation(x, y);
			n.mass = 1.0f;
			n.velocity.x = 0.0;
			n.velocity.y = 0.0;
		}
		
	}
}
