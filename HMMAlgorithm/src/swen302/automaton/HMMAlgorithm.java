package swen302.automaton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import swen302.graph.Graph;
import swen302.tracer.Trace;
import swen302.tracer.TraceEntry;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;

/**
 *Main Method to read a given trace file and produce a graph of nodes
 * @author Oliver Greenaway, Marian Clements
 *
 */

public class HMMAlgorithm implements VisualizationAlgorithm, IncrementalVisualizationAlgorithm {

	public static Set<String> callHierarchy = new HashSet<String>();
	public Map<String,State> states = new HashMap<String,State>();

	@Override
	public void startIncremental() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean processLine(TraceEntry line) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Graph[] getCurrentGraphs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph[] generateGraph(Trace[] traces) {
		Graph g = new Graph();

		//TODO gather initial data
		String prevMethod = null;
		for(Trace trace : traces){
			for(TraceEntry entry : trace.lines){
				if(prevMethod == null){
					prevMethod = entry.method.name;
					State state = new State();
					state.stateName = prevMethod;
					state.first = true;
					states.put(prevMethod, state);
				}else{
					String name = entry.method.name;
					if(!name.equals(prevMethod)){
						states.get(prevMethod).methodCalls.add(name);
						if(states.containsKey(name)){
							prevMethod = name;
						}else{
							prevMethod = entry.method.name;
							State state = new State();
							state.stateName = prevMethod;
							states.put(prevMethod, state);
						}
					}else{
						states.get(prevMethod).methodCalls.add(name);
					}
				}
				callHierarchy.add(prevMethod);
			}
		}

		String filename = "test.txt";
		try {
			/*PrintStream print = new PrintStream(new File(filename));
			//TODO print to file
			print.println("Hmm v1.0");
			print.println();
			print.println("NbStates "+callHierarchy.size());
			print.println();
			for(String call : callHierarchy){
				State curState = states.get(call);
				print.println("State");
				print.println("Pi "+(curState.first?"1":"0"));
				print.print("A ");
				for(float f : curState.processMethodCallProbs()){
					print.print(f+" ");
				}
				print.print("\n");
				print.println("IntegerOPDF [0.5 0.5 ]");
				print.println();
			}
			print.close();*/
			double[] pi = new double[callHierarchy.size()];
			double[][] a = new double[callHierarchy.size()][callHierarchy.size()];
			List<OpdfInteger> opdfs = new ArrayList<OpdfInteger>();

			int index = 0;
			for(String call : callHierarchy){
				State curState = states.get(call);
				pi[index] = curState.first?1:0;
				int index2 = 0;
				for(float f : curState.processMethodCallProbs()){
					a[index][index2] = f;
					index2++;
				}
				opdfs.add(index, new OpdfInteger(new double[]{0.5,0.5}));
				index++;
			}

			Hmm<Observation> hmm = new Hmm<Observation>(pi, a, (List<? extends Opdf<Observation>>) opdfs);

			//TODO run algorithm
			BaumWelchLearner learner = new BaumWelchLearner();
			learner.setNbIterations(500);
			ArrayList<List<Observation>> sequen = new ArrayList<List<Observation>>();
			List<Observation> obs = new ArrayList<Observation>();

			for(int i=0; i<callHierarchy.size(); i++){
				obs.add(new ObservationInteger(1));
			}
			//for(String s : callHierarchy){
				sequen.add(obs);
			//}
			Hmm<Observation> newhmm = learner.learn(hmm, sequen);

			//Scanner scan = new Scanner(new File(filename));
			//TODO Build graph
			//scan.close();
			//new File(filename).delete();
			new GenericHmmDrawerDot().write(newhmm, "newhmm.dot");
			new GenericHmmDrawerDot().write(hmm, "hmmorig.dot");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new Graph[] {g};
	}



}
