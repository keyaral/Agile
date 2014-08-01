package swen302.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
	public List<Node> nodes = new ArrayList<Node>();
	public List<Edge> edges = new ArrayList<Edge>();


	public static Graph createFromNodes(Collection<? extends Node> nodes) {
		Set<Edge> edges = new HashSet<Edge>();
		for(Node n : nodes)
			edges.addAll(n.getConnections().values());

		Graph g = new Graph();
		g.nodes.addAll(nodes);
		g.edges.addAll(edges);
		return g;
	}
}
