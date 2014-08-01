package swen302.graph;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 *Graph Drawer Class
 *
 *Takes a graph of Nodes and converts into dot language code and saves as a txt files.
 *
 *Runs the dot language command to convert the text file into a png.
 *
 * @author Oliver Greenaway, Marian Clements
 *
 */
public class GraphSaver {

	public static void save(Graph graph, File dotfile, File pngfile) throws InterruptedException, IOException {
		save(graph.nodes, dotfile, pngfile);
	}

	/**
	 * Runs the save process with final output of a png file displaying the graph
	 * @param nodes Nodes making up the graph.
	 * @throws InterruptedException, IOException
	 */
	private static void save(List<Node> nodes, File dotfile, File pngfile) throws InterruptedException, IOException {

		try (PrintStream print = new PrintStream(dotfile)) {   //converting Nodes into dot language txt file

			print.println("digraph g{");
			for(Node n : nodes){
				print.println(n.getID()+"[label=\""+n.getLabel()+"\"]"+";");
				Map<Node,Edge> connections = n.getConnections();
				for(Node n2 : connections.keySet()){
					print.println(n.getID()+"->"+n2.getID()+" [label=\""+connections.get(n2).shortname+"\"];");
				}
			}

			print.println("}");
		}


		// dot language Command to produce png

		Process p = Runtime.getRuntime().exec(new String[] {"fdp", "-Tpng", "-o"+pngfile.getAbsolutePath(), dotfile.getAbsolutePath()});
		p.waitFor();
		if(p.exitValue() != 0)
			throw new IOException("fdp command failed");

	}

	public static void save(Graph graph, File pngfile) throws InterruptedException, IOException {
		save(graph, File.createTempFile("swen302-", ".dot"));
	}

}
