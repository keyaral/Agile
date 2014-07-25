package swen302.automaton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Graph {

	public void save(List<Node> nodes) throws IOException, InterruptedException{
		PrintStream print = new PrintStream(new File("test.txt"));
		print.println("digraph g{");
		for(Node n : nodes){
			print.println(n.getLabel()+";");
			Map<Node,Transition> connections = n.getConnections();
			for(Node n2 : connections.keySet()){
				print.println(n.getLabel()+"->"+n2.getLabel()+" [label=\""+connections.get(n2).shortname+"\"];");
			}
		}

		print.println("}");
		print.close();


		Process p = Runtime.getRuntime().exec("fdp -Tpng test.txt");
		//p.waitFor();


		PrintStream output = new PrintStream(new File("test.png"));
		InputStream reader = p.getInputStream();


		int line = 0;
		while ((line = reader.read())!= -1) {
			output.write(line);
		}

	}

}
