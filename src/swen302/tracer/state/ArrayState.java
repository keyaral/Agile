package swen302.tracer.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swen302.tracer.TraceFilter;

public class ArrayState extends State {
	private static final long serialVersionUID = 1L;

	public List<State> values = new ArrayList<State>();

	@Override
	public String toString() {
		return toString(new HashMap<State, String>());
	}

	public String toString(Map<State, String> alreadySeenObjects) {
		if(alreadySeenObjects.containsKey(this))
			return alreadySeenObjects.get(this);
		alreadySeenObjects.put(this, "OBJ"+alreadySeenObjects.size());

		StringBuilder result = new StringBuilder();
		result.append('[');
		boolean first = true;
		for(State v : values) {
			if(!first) result.append(',');
			else first = false;
			result.append(v.toString(alreadySeenObjects));
		}
		result.append(']');
		return result.toString();
	}

	@Override
	public void filterFields(TraceFilter f) {
		for(State s : values)
			s.filterFields(f);
	}
}
