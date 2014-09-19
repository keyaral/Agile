package swen302.automaton;

import java.util.Stack;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.LabelFormatOptions;
import swen302.graph.Node;
import swen302.tracer.Trace;
import swen302.tracer.TraceEntry;

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
			for(TraceEntry line : t.lines) {
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
		graph.addNode(currentNode);
	}

	@Override
	public Graph getCurrentGraph() {
		return graph;
	}


	Stack<Node> stack;
	int nodeCount;
	Node currentNode;




	@Override
	public boolean processLine(TraceEntry line) {

		if(line.state != null) { //Updates state of next node
			currentNode.setState(line.state.toString());
		}

		if(!line.isReturn) { // Reads an instance of a method call
			stack.push(currentNode);
			currentNode = new Node(String.format("%d", nodeCount++));
			graph.addNode(currentNode);
			graph.addEdge(new Edge(line.getLongMethodName(), AutomatonGraphUtils.createMethodLabelObject(line.getLongMethodName(), line.arguments), stack.peek(), currentNode));

		} else if(stack.size() > 1) { // Reads an instance of return call.

			Node temp = currentNode;
			currentNode = stack.pop();
			graph.addEdge(new Edge(line.getLongMethodName(), LabelFormatOptions.displayMethod ? "Return" : "", temp, currentNode));
		}

		return true;

	}

	@Override
	public String toString() {
		return "Simple call tree (no merging)";
	}

}
