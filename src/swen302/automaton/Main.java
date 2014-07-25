package swen302.automaton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;


public class Main {

	public Main(String filename){
		buildGraph(filename);
		System.out.println("Graph Complete");
		try {
			new Graph().save(allNodes);
			System.out.println("Image Complete");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private List<Node> allNodes = new ArrayList<Node>();

	private void buildGraph(String filename) {
		try {
			Scanner in = new Scanner(new File(filename));
			Stack<Node> stack = new Stack<Node>();
			int nodeCount = 0;
			Node currentNode = new Node(String.format("%d", nodeCount++));
			allNodes.add(currentNode);
			while(in.hasNextLine()){
				String line = in.nextLine();
				if(isMethod(line)){
					Method m = new Method(getLongMethodName(line), getShortMethodName(line));
					stack.push(currentNode);
					currentNode = new Node(String.format("%d", nodeCount++));
					allNodes.add(currentNode);
					stack.peek().addNode(m,currentNode);

				}else if(isReturn(line) && stack.size()>1){
					Return r = new Return(getLongReturnName(line), getShortReturnName(line));
					Node temp = currentNode;
					currentNode = stack.pop();
					temp.addNode(r, currentNode);

				}
			}


			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}



	private String getShortReturnName(String line) {
		//line = getLongReturnName(line);
		//String[] lineArray = line.split(".");
		//String toReturn = lineArray[lineArray.length-2]+"."+lineArray[lineArray.length-1];
		return "Return";
	}

	private String getLongReturnName(String line) {
		line = line.substring(8);
		String[] lineArray = line.split(" ");
		String toReturn = "";
		for(int i=0; i<lineArray.length-1; i++){
			toReturn += lineArray[i];
		}
		return toReturn;
	}

	private String getShortMethodName(String line) {
		line = getLongMethodName(line);
		String[] lineArray = line.split("\\.");
		
		String toReturn = "";
		
		for (int i=0; i< lineArray.length; i++ ){
		if(lineArray[i].contains("(")){
			
		}	
		
		 
		} 
		
		return toReturn;
	}

	private String getLongMethodName(String line) {
		line = line.substring(20);
		String[] lineArray = line.split(" ");
		String toReturn = "";
		for(int i=0; i<lineArray.length-2; i++){
			toReturn += lineArray[i];
		}
		return toReturn;
	}

	private boolean isReturn(String line) {
		return line.startsWith("Call to ");
	}

	private boolean isMethod(String line) {
		return line.startsWith("Intercepted call to ");
	}

	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("Program requires file name as argument.");
		}else{
			new Main(args[0]);
		}
	}

}
