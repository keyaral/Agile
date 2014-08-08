package swen302.vertexgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import swen302.graph.Graph;
import swen302.graph.Node;

public class VertexGraph {

	public List<Vertex> vertices;
	
	public VertexGraph(Graph graph) {
		
		this.vertices = new ArrayList<Vertex>();
		for (Node n : graph.nodes) {
			this.vertices.add(new Vertex(n.getID(), n.getLabel()));	
		}
		
		
	}
	
	public void generateInitialLayout(int width, int height) {
		Random rand = new Random();
		
		for (Vertex v : vertices) {
			v.position.setLocation(rand.nextInt(width), rand.nextInt(height));
			v.mass = 1.0f;
			v.velocity.x = rand.nextDouble()*10;
			v.velocity.y = rand.nextDouble()*10;
		}
		
	}
}
