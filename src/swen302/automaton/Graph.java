package swen302.automaton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
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
public class Graph {

	/**
	 * Runs the save process with final output of a png file displaying the graph
	 * @param nodes Nodes making up the graph.
	 */
	public void save(Collection<Node> nodes){

		try {
			PrintStream print = new PrintStream(new File("test.txt"));   //converting Nodes into dot language txt file

			print.println("digraph g{");
			for(Node n : nodes){
				print.println(n.getID()+"[label=\""+n.getLabel()+"\"]"+";");
				Map<Node,Transition> connections = n.getConnections();
				for(Node n2 : connections.keySet()){
					print.println(n.getID()+"->"+n2.getID()+" [label=\""+connections.get(n2).shortname+"\"];");
				}
			}

			print.println("}");
			print.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		// dot language Command to produce png

		try {
			Process p = Runtime.getRuntime().exec("fdp -Tpng test.txt");
			PrintStream output = new PrintStream(new File("test.png"));
			InputStream reader = p.getInputStream();



			int line = 0;
			while ((line = reader.read())!= -1) {
				output.write(line);
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
