package swen302.graph;

import java.util.Collection;
import java.util.HashSet;
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
}
