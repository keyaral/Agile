package swen302.automaton.splitpetrinet;

import swen302.automaton.FieldBasedAlgorithm;
import swen302.automaton.PetriNetAlgorithm;
import swen302.automaton.VisualizationAlgorithm;
import swen302.graph.Graph;
import swen302.tracer.Trace;

public class SplitScreenPetriNetAlgorithm implements VisualizationAlgorithm {

	PetriNetAlgorithm pna = new PetriNetAlgorithm();
	FieldBasedAlgorithm fba = new FieldBasedAlgorithm();

	Graph pnGraph, fbGraph;

	@Override
	public Graph[] generateGraph(Trace[] traces) {

		pnGraph = pna.generateGraph(traces)[0];
		fbGraph = fba.generateGraph(traces)[0];

		return new Graph[] {pnGraph, fbGraph};
	}

	@Override
	public String toString() {
		return "Split-Screen Petri Net Algorithm";
	}

}

