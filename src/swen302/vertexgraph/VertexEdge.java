package swen302.vertexgraph;

import swen302.graph.Edge;

public class VertexEdge extends Edge {
	
	public Vertex v1;
	public Vertex v2;
	
	public VertexEdge(String longname, String shortname, Vertex v1, Vertex v2){
		super(longname, shortname);
		this.v1 = v1;
		this.v2 = v2;
	}
}
