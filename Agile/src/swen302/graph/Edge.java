package swen302.graph;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;


/**
 *SuperClass to describe the relation between two nodes to represent a trace call
 *
 * @author Oliver Greenaway, Marian Clements
 *
 */

public class Edge {

	public String id;
	public Object label;
	public final Node node1, node2;
	public int duplicateCount = 0;

	public boolean highlighted;

	public Vector2D arrowAngle; // used by EadesSpringEmbedder only
	public Vector2D arrowPt; // used by EadesSpringEmbedder only


	public Edge(String id, Object label, Node node1, Node node2){
		this.id = id;
		this.label = label;
		this.node1 = node1;
		this.node2 = node2;
	}

	public Edge(Edge copyFrom, Node node1, Node node2) {
		this(copyFrom.id, copyFrom.label, node1, node2);
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Edge){
			Edge o = (Edge)obj;
			return o.label.equals(label) && o.node1 == node1 && o.node2 == node2;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return label.hashCode();
	}

	public Node getOtherNode(Node n) {
		if(n == node1) return node2;
		if(n == node2) return node1;
		throw new IllegalArgumentException("Node given ("+n+") is not first node ("+node1+") or second node ("+node2+")");
	}


}
