package swen302.execution;

import java.io.Serializable;

/**
 * We might be using multiple different traces to generate an automaton.
 *
 * ExecutionData contains the data that is different for each program execution
 * (e.g. command-line arguments).
 *
 * @author campbealex2
 */
public class ExecutionData implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public String commandLineArguments = "";

	@Override
	public String toString() {
		return "Args: "+commandLineArguments;
	}

	@Override
	public ExecutionData clone() {
		try {
			return (ExecutionData)super.clone();
		} catch(CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
}
