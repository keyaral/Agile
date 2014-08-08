package swen302.automaton;

import java.util.Arrays;
import java.util.List;

import swen302.graph.Graph;
import swen302.tracer.Trace;

public interface VisualizationAlgorithm {
	public Graph generateGraph(Trace[] trace);

	/** Returns the name of the algorithm. */
	public String toString();

	public static final List<Class<? extends VisualizationAlgorithm>> ALGORITHMS = Arrays.asList(
		CallTreeAlgorithm.class,
		FieldBasedAlgorithm.class,
		KTailsAlgorithm.class);
}
