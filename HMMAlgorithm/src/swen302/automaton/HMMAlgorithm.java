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

public class HMMAlgorithm implements VisualizationAlgorithm{

	public static Set<String> callHierarchy = new HashSet<String>();
	public Map<String,State> states = new HashMap<String,State>();

	public List<List<ObservationInteger>> multipleTraces = new ArrayList<List<ObservationInteger>>();
	public Map<String,Integer> methodMapping = new HashMap<String,Integer>();

	@Override
	public Graph generateGraph(Trace[] traces) {

		try {


			String prevMethod = null;
			int count = 0;
			for(Trace trace : traces){
				List<ObservationInteger> curTrace = new ArrayList<ObservationInteger>();
				for(TraceEntry entry : trace.lines){
					String s = entry.method.name;
					if(methodMapping.containsKey(s)){
						curTrace.add(new ObservationInteger(methodMapping.get(s)));
					}else{
						methodMapping.put(s, count++);
						curTrace.add(new ObservationInteger(methodMapping.get(s)));
					}
				}
				multipleTraces.add(curTrace);
			}

			KMeansLearner<ObservationInteger> kml = new KMeansLearner<ObservationInteger>(10, new OpdfIntegerFactory(11), (List<? extends List<? extends ObservationInteger>>) multipleTraces);



			Hmm<ObservationInteger> newhmm = kml.learn();
			new GenericHmmDrawerDot().write(newhmm, "hmm.dot");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String toString(){
		return "HMM Algorithm";
	}



}
