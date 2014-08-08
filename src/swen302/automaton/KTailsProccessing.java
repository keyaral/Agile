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
	 * Calls the trace reader method, and then calls the graph drawer.
	 * @param filename
	 */
//	public KTailsMain(/*String filename*/){
//		buildGraph("abcd");
//		System.out.println("Graph Complete");
//
//		try {
//			GraphSaver.save(graph,new File("output.png"));
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Image Complete");
//
//	}

	public KTailsProccessing(Trace input){
		System.out.println("Start Main");
		buildGraph(input);
		System.out.println("End Main");
	}

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
			//Scanner in = new Scanner(new File(filename));
			//Scanner in = new Scanner(Tracer.Trace("-cp bin", "swen302.testprograms.StringParser "+input, "swen302\\.testprograms\\.StringParser.*"));
			Stack<Node> stack = new Stack<Node>();
			int nodeCount = 0;
			Node currentNode = new Node(String.format("%d", nodeCount++));

			graph = new Graph();
			graph.nodes.add(currentNode);

			for(String line : input.lines){//Tracer.Trace("-cp bin", "swen302.testprograms.StringParser "+input, new RegexTraceMethodFilter("swen302\\.testprograms\\.StringParser.method[A-Z]")).lines){

				if(isMethod(line)){  // Reads an instance of a method call

					//if(m.shortname.contains("method")){
						stack.push(currentNode);
						currentNode = new Node(String.format("%d", nodeCount++));
						graph.nodes.add(currentNode);
						graph.addEdge(new Method(getLongMethodName(line), getShortMethodName(line), stack.peek(), currentNode));
					//}

				}else if(isReturn(line) && stack.size()>1){ // Reads an instance of return call.
					currentNode = stack.pop();

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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
