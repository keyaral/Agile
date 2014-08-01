package swen302.graph;

/**
 *SuperClass to describe the relation between two nodes to represent a trace call
 *
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class Edge {

	public String longname,shortname;

	public Edge(String longName, String shortName){
		this.longname = longName;
		this.shortname = shortName;
	}
}
