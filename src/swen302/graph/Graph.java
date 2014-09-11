package swen302.graph;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graph {
	private Set<Node> nodes_modifiable = new HashSet<Node>();

	public Set<Node> nodes = Collections.unmodifiableSet(nodes_modifiable);
	public Set<Edge> edges = new HashSet<Edge>();

	private List<GraphListener> listeners = new CopyOnWriteArrayList<GraphListener>();

	public void addListener(GraphListener l) {listeners.add(l);}
	public void removeListener(GraphListener l) {listeners.remove(l);}


	public static Graph createFromNodes(Collection<? extends Node> nodes) {
		Set<Edge> edges = new HashSet<Edge>();

		for(Node n : nodes) {
			edges.addAll(n.getConnections());
			for(Edge e : n.getConnections()){
				Node n2 = e.getOtherNode(n);
				boolean lol = n2.getConnections().contains(n);
				if(lol)
					System.out.println("WORKS");
			}
		}

		Graph g = new Graph();
		g.nodes_modifiable.addAll(nodes);
		g.edges.addAll(edges);
		return g;
	}

	public void addEdge(Edge edge) {
		edges.add(edge);
		edge.node1.addOutgoingEdge(edge);
	}

	public void addNode(Node n) {
		nodes_modifiable.add(n);
		for(GraphListener l : listeners)
			l.onNodeAdded(n);
	}

	public void generateInitialLayout(int width, int height, Graphics graphics) {
		Random rand = new Random();

		for (Node n : nodes) {
			n.randPosition(rand);

			n.mass = 1.0f;
		}

		onLabelsChanged(graphics);
	}

	public void replicateLayout(int width, int height, Graph g){
		Random rand = new Random();
		for(Node n : this.nodes){
			if(!g.nodes.contains(n)){
				n.randPosition(rand);
				n.mass = 1.0f;
			}
		}
		//onLabelsChanged(graphics); //TODO clean
	}

	public void onLabelsChanged(Graphics graphics) {
		for (Node n : nodes) {
			FontMetrics fm = graphics.getFontMetrics();
			n.setLabel(fm.getStringBounds(n.getLabel(), graphics));
		}
	}

}
