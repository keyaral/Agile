package swen302.automaton;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.GraphSaver;
import swen302.tracer.Trace;
import swen302.tracer.Tracer;
import swen302.graph.Node;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class FieldBasedAlgorithm implements VisualizationAlgorithm {

	/**
	 * Calls the trace reader method, and then calls the graph drawer.
	 * @param filename
	 */
//	public AutomatonBuilder2(String filename) throws Exception{
//		buildGraph(filename);
//		System.out.println("Graph Complete");
//
//		new Graph().save(allNodes);
//		System.out.println("Image Complete");
//
//	}

	private Collection<Node> allNodes = new HashSet<Node>(); // Graph of Nodes
	private Map<String, Node> states = new HashMap<String, Node>();

	private int nodeCount = 0;

	private Node getStateNode(String state) {
		if(!states.containsKey(state)){
			Node n = new Node(String.valueOf(nodeCount++));
			n.setState(state);
			states.put(state, n);
		}
		return states.get(state);

	}

	/**
	 * Build Graph reads a trace file to construct the graph of Nodes
	 *
	 * @param filename
	 */

	private void buildGraph(Trace trace) {
		Stack<Node> stack = new Stack<Node>();
		int nodeCount = 0;

		for (String line : trace.lines) {

			if (isStateCall(line) ) { //Updates state of next node
				if (line.startsWith("staticContext")) {
					stack.push(null);
				}else{
					stack.push(getStateNode(line.substring(12)));
				}

			}
			else if(isMethod(line)){  // Reads an instance of a method call

			}
			else if(isReturn(line) && stack.size()>=1){ // Reads an instance of return call.

				Node finalState = stack.pop();
				Node initState = stack.pop();

				if (finalState != null && initState != null) {
					initState.addNode(new Edge(String.valueOf(nodeCount++), getLongReturnName(line)), finalState);

					allNodes.add(finalState);
					allNodes.add(initState);
				}
			}
		}
	}


	/**
	 * Returns whether the line is a state call
	 * @param line
	 * @return
	 */
	private boolean isStateCall(String line) {
		return line.startsWith("staticContext") || line.startsWith("objectState");
	}


	/**
	 * Returns a label for the return instance
	 * ( Return methods are displayed as "return" )
	 * @param line
	 * @return
	 */
	private String getShortReturnName(String line) {
		return "Return";
	}

	/**
	 * Returns the long name of a return method call.
	 * @param line
	 * @return
	 */
	private String getLongReturnName(String line) {
		return line.substring(7);
	}


	/**
	 * Returns a label for the return instance
	 * ( Method calls are displayed as "a shortened version of the trace call" )
	 *
	 * @param line
	 * @return
	 */
	private String getShortMethodName(String line) {
		line = getLongMethodName(line);

		String[] lineArrayS = line.split(" ");

		String met = lineArrayS[1];

		String[] lineArray = lineArrayS[0].split("\\.");


		return lineArray[lineArray.length-1] + " " + met;
	}
	/**
	 * Returns the long name for a method call
	 * @param line
	 * @return
	 */
	private String getLongMethodName(String line) {
		return line.substring(11);
	}


	/**
	 * Boolean to assert line is a return call
	 * @param line
	 * @return
	 */
	private boolean isReturn(String line) {
		return line.startsWith("return");
	}

	/***
	 * Boolean to assert line is a method call
	 * @param line
	 * @return
	 */
	private boolean isMethod(String line) {
		return line.startsWith("methodCall");
	}

	@Override
	public Graph generateGraph(Trace trace) {

		buildGraph(trace);

		Graph g = new Graph();
		g.nodes.addAll(allNodes);
		for(Node n : allNodes) {
			g.edges.removeAll(n.getConnections().values());
			g.edges.addAll(n.getConnections().values());
		}

		return g;
	}

}
