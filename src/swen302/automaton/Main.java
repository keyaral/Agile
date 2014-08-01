package swen302.automaton;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class Main implements VisualizationAlgorithm {

	private List<Node> allNodes = new ArrayList<Node>(); // Graph of Nodes

	@Override
	public Graph generateGraph(Trace trace) {
		allNodes.clear();
		buildGraph(trace.lines);
		Graph graph = new Graph();
		graph.nodes.addAll(allNodes);
		for(Node n : allNodes) {
			graph.edges.removeAll(n.getConnections().values());
			graph.edges.addAll(n.getConnections().values());
		}
		return graph;
	}



	/**
	 * Build Graph reads a trace file to construct the graph of Nodes
	 *
	 * @param filename
	 */
	private void buildGraph(List<String> lines) {
		Stack<Node> stack = new Stack<Node>();
		int nodeCount = 0;
		Node currentNode = new Node(String.format("%d", nodeCount++));


		allNodes.add(currentNode);
		Iterator<String> in = lines.iterator();
		while(in.hasNext()){
			String line = in.next();

			if (isStateCall(line) ) { //Updates state of next node
				if (line.startsWith("staticContext")) {

				}else{
					currentNode.setState(line.substring(12) );
				}


			}
			else if(isMethod(line)){  // Reads an instance of a method call

				Method m = new Method(getLongMethodName(line), getShortMethodName(line));
				stack.push(currentNode);
				currentNode = new Node(String.format("%d", nodeCount++));
				allNodes.add(currentNode);
				stack.peek().addNode(m,currentNode);

			}else if(isReturn(line) && stack.size()>1){ // Reads an instance of return call.
				Return r = new Return(getLongReturnName(line), getShortReturnName(line));
				Node temp = currentNode;
				currentNode = stack.pop();
				temp.addNode(r, currentNode);

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

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if(args.length != 1){
			System.out.println("Program requires file name as argument.");
		}else{
			Graph g = new Main().generateGraph(Trace.readFile(args[0]));
			System.out.println("Graph Complete");

			GraphSaver.save(g, new File("test.txt"), new File("test.png"));
			System.out.println("Image Complete");
		}
	}

}
