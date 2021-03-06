package swen302.automaton;

import swen302.graph.Graph;
import swen302.tracer.Trace;

public interface VisualizationAlgorithm {
	public Graph[] generateGraph(Trace[] trace);

	/** Returns the name of the algorithm. */
	public String toString();
}
