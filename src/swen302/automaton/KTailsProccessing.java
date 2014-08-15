package swen302.automaton;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.tracer.Trace;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class KTailsProccessing {



	/**
	 * Builds a graph from a given trace
	 * @param input
	 */
	public KTailsProccessing(Trace input){
		buildGraph(input);

	}

	/**
	 * Returns all the nodes created from a trace
	 * @return
	 */
	public List<Node> getNodes(){
		return new ArrayList<Node>(graph.nodes);
	}

	private Graph graph;



	/**
	 * Build Graph reads a trace file to construct the graph of Nodes
	 *
	 * @param filename
	 */
	private void buildGraph(Trace input) {
		try {
			Stack<Node> stack = new Stack<Node>();
			int nodeCount = 0;
			Node currentNode = new Node(String.format("%d", nodeCount++));

			graph = new Graph();
			graph.nodes.add(currentNode);

			for(String line : input.lines){
				if(isMethod(line)){  // Reads an instance of a method call

					stack.push(currentNode);
					currentNode = new Node(String.format("%d", nodeCount++));
					graph.nodes.add(currentNode);
					graph.addEdge(new Method(getLongMethodName(line), getShortMethodName(line), stack.peek(), currentNode));


				}else if(isReturn(line) && stack.size()>1){ // Reads an instance of return call.
					Return r = new Return(getLongReturnName(line), getShortReturnName(line),stack.peek(),currentNode);
					Node temp = currentNode;
					currentNode = stack.pop();

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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

		String[] lineArrayMain = lineArray[lineArray.length-1].split("\\$");


		return lineArrayMain[lineArrayMain.length-1] + " " + met;
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

}
