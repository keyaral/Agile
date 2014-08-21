package swen302.automaton;

import java.io.File;
import java.util.Stack;

import swen302.graph.Graph;
import swen302.graph.GraphSaver;
import swen302.graph.Node;
import swen302.tracer.Trace;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */

public class CallTreeAlgorithm implements VisualizationAlgorithm, IncrementalVisualizationAlgorithm {

	private Graph graph;

	@Override
	public Graph generateGraph(Trace[] traces) {
		startIncremental();
		boolean first = true;
		for(Trace t : traces) {
			if(first) first = false;
			else startTrace();
			for(String line : t.lines) {
				processLine(line);
			}
		}
		return getCurrentGraph();
	}


	@Override
	public void startIncremental() {
		graph = new Graph();
		nodeCount = 0;
		startTrace();
	}

	private void startTrace() {
		stack = new Stack<Node>();
		currentNode = new Node(String.format("%d", nodeCount++));
		graph.nodes.add(currentNode);
	}

	@Override
	public Graph getCurrentGraph() {
		return graph;
	}


	Stack<Node> stack;
	int nodeCount;
	Node currentNode;




	@Override
	public boolean processLine(String line) {

		if (isStateCall(line) ) { //Updates state of next node
			if (line.startsWith("staticContext")) {
				return false;
			}else{
				currentNode.setState(line.substring(12) );
				return true;
			}



		}
		else if(isMethod(line)){  // Reads an instance of a method call

			stack.push(currentNode);
			currentNode = new Node(String.format("%d", nodeCount++));
			graph.nodes.add(currentNode);
			graph.addEdge(new Method(getLongMethodName(line), AutomatonGraphUtils.formatMethodLabel(getLongMethodName(line)), stack.peek(), currentNode));

			return true;

		}else if(isReturn(line) && stack.size()>1){ // Reads an instance of return call.
			Node temp = currentNode;
			currentNode = stack.pop();
			graph.addEdge(new Return(getLongReturnName(line), "Return", temp, currentNode));

			return true;

		}

		return false;

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
	 * Returns the long name of a return method call.
	 * @param line
	 * @return
	 */
	private String getLongReturnName(String line) {
		return line.substring(7);
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

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if(args.length != 1){
			System.out.println("Program requires file name as argument.");
		}else{
			Graph g = new CallTreeAlgorithm().generateGraph(new Trace[]{Trace.readFile(args[0])}); //TODO fix
			System.out.println("Graph Complete");

			GraphSaver.save(g, new File("test.txt"), new File("test.png"));
			System.out.println("Image Complete");
		}
	}



	@Override
	public String toString() {
		return "Simple call tree (no merging)";
	}

}
