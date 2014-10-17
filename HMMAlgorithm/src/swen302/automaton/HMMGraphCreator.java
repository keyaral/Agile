package swen302.automaton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;

public class HMMGraphCreator {
	protected double minimumAij = 0.01;
    protected double minimumPi = 0.01;
    protected NumberFormat probabilityFormat;
    private Graph output = new Graph();
    private List<Node> nodesByNumber = new ArrayList<Node>();
    private List<Edge> edges = new ArrayList<Edge>();
    private int NumbClus = 10;

	 public HMMGraphCreator()
     {
             probabilityFormat = NumberFormat.getInstance();
             probabilityFormat.setMaximumFractionDigits(2);
     }


	 /**
	  * Is given an Hmm to convert into a graph
	  *
	  * Fist creates the nodes, then creates the edges.
	  *
	  * Then adds all of them to the graph to return
	  * @param hmm
	  * @return
	  */
	 protected Graph convert(Hmm<?> hmm)
     {

		states(hmm);
        transitions(hmm);


        for( Node n : nodesByNumber){
        	output.addNode(n);

        }
        for (Edge e : edges){
        	output.addEdge(e);
        }

             return output;


     }


	 /**
	  * Iterates through the Hma States to create the edges with the correct Nodes and Label
	  * @param hmm
	  */
     protected void transitions(Hmm<?> hmm)
     {
    	 int EdgeID = 0;
             String s = "";

             for (int i = 0; i < hmm.nbStates(); i++) {

                     for (int j = 0; j < hmm.nbStates(); j++) {
                    	 if (hmm.getAij(i, j) >= minimumAij) {
                    	 Edge e = new Edge(  Integer.toString(EdgeID++), probabilityFormat.format(hmm.getAij(i, j)),
                    			 nodesByNumber.get(i), nodesByNumber.get(j) );

                    	 edges.add(e);
                    	 }
                     }

             }

     }

     /**
	  * Iterates through the Hma States to create the Nodes with the correct Labels
	  * @param hmm
	  */
     protected void states(Hmm<?>  hmm)
     {
             String s = "";
             Node newNode;

             for (int i = 0; i < hmm.nbStates(); i++) {
                     s += "\t" + i + " [";

                     if (hmm.getPi(i) >= minimumPi) {

                    	 newNode = new Node( Integer.toString(i) );
                    	newNode.setLabel(""+i + "Pi= " + probabilityFormat.format(hmm.getPi(i)) + " - " +
                                opdfLabel(hmm, i) + "]");

                           //  += "shape=doublecircle, label=\"" + i +
                           //  " -  + "\"";
                     } else {
                    	 newNode = new Node( Integer.toString(i) );
                    		newNode.setLabel("" + i + " - " +
                             opdfLabel(hmm, i) + "]");
                     }
                     nodesByNumber.add(newNode);
             }


     }


     protected String opdfLabel(Hmm<?>  hmm, int stateNb)
     {
             return "[ " + hmm.getOpdf(stateNb).toString() + " ]";
     }


}
