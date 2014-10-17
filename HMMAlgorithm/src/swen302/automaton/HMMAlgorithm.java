package swen302.automaton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import swen302.graph.Graph;
import swen302.tracer.Trace;
import swen302.tracer.TraceEntry;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */
/**
 * This method takes set of traces and processes it uses the HMA Algorithm.
 * This uses the library found at http://code.google.com/p/jahmm/
 * The Package Name is be.ac.ulg.montefiore.run.jahmm.
 *
 * The HMA Algorithm uses a learning Algotihm KMeans, and it is configured to use ItergersObervations
 *
 * To fallitate this, we have processed the trace to record the methods in a map.
 * The first time a method is seen, it is asssigned a number.
 * The trace is then turned into an ArrayList of numbers, where each number represents a method
 *
 * The Algorithm can process mutliple traces, as each trace is stored as its own list,
 * and the Kmean Learner takes an List<List<intergerObersations>>.
 * @author greenaoliv
 *
 */
public class HMMAlgorithm implements VisualizationAlgorithm{

	public static Set<String> callHierarchy = new HashSet<String>();
	public Map<String,State> states = new HashMap<String,State>();

	public List<List<ObservationInteger>> multipleTraces = new ArrayList<List<ObservationInteger>>();
	public Map<String,Integer> methodMapping = new HashMap<String,Integer>();

	@Override
	public Graph[] generateGraph(Trace[] traces) {




			int count = 0;   // Index for numbering methofs
			for(Trace trace : traces){   // Iterates through a list of Traces
				List<ObservationInteger> curTrace = new ArrayList<ObservationInteger>();
				for(TraceEntry entry : trace.lines){ // Each entry will have one method call to record
					String s = entry.method.name;
					if(methodMapping.containsKey(s)){ // Method has been seen before
						curTrace.add(new ObservationInteger(methodMapping.get(s)));
					}else{ // First occurace of method, Put in map with an index number
						methodMapping.put(s, count++);
						curTrace.add(new ObservationInteger(methodMapping.get(s)));
					}
				}
				multipleTraces.add(curTrace);
			}


			// This runs the KMeans Learner.
			// It is hardcoded to produce 10 States.
			//The OpdfIntegerFactory must be set to One higher than the number of states

			KMeansLearner<ObservationInteger> kml = new KMeansLearner<ObservationInteger>(10, new OpdfIntegerFactory(11), (List<? extends List<? extends ObservationInteger>>) multipleTraces);
			Hmm<ObservationInteger> newhmm = kml.learn();

			HMMGraphCreator graphMaker = new HMMGraphCreator();

			Graph[] output = new Graph[1];
			output[0] = graphMaker.convert(newhmm);


			/// CHANGE DOT LANGUAGE INTO





// Does not currently return a graph. Instruction to use the dot lanague are provided.
		return output;
	}

	@Override
	public String toString(){
		return "HMM Algorithm";
	}



}
