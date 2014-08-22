package swen302.tracer.state;

import java.io.Serializable;
import java.util.Map;

import swen302.tracer.TraceFieldFilter;

public class State implements Serializable {
	private static final long serialVersionUID = 1L;

	public void filterFields(TraceFieldFilter f) {
	}

	public String toString(Map<State, String> alreadySeenObjects) {
		return toString();
	}
}
