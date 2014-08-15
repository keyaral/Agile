package swen302.automaton;

import swen302.graph.Graph;

public interface IncrementalVisualizationAlgorithm {

	/**
	 * Called before this algorithm starts being used incrementally.
	 */
	public void startIncremental();

	/**
	 * Processes a trace line.
	 * @param line The trace line.
	 * @return True if the graph changed and needs to be redrawn.
	 */
	public boolean processLine(String line);

	/**
	 * @return The current state of the graph. This might be a static copy of the graph,
	 *         or it might be a graph that's updated by processLine.
	 */
	public Graph getCurrentGraph();
}
