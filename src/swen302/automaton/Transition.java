package swen302.automaton;

/**
 *SuperClass to describe the relation between two nodes to represent a trace call
 *
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class Transition{

	public String longname,shortname;

	public Transition(String longName, String shortName){
		this.longname = longName;
		this.shortname = shortName;
	}
}
