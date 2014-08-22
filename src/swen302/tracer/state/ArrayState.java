package swen302.tracer.state;

import java.util.ArrayList;
import java.util.List;

import swen302.tracer.TraceFieldFilter;

public class ArrayState extends State {
	private static final long serialVersionUID = 1L;

	public List<State> values = new ArrayList<State>();

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append('[');
		boolean first = true;
		for(State v : values) {
			if(!first) result.append(',');
			else first = false;
			result.append(v.toString());
		}
		result.append(']');
		return result.toString();
	}

	@Override
	public void filterFields(TraceFieldFilter f) {
		for(State s : values)
			s.filterFields(f);
	}
}
