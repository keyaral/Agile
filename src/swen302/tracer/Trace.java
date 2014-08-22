package swen302.tracer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trace implements Serializable {
	private static final long serialVersionUID = 1L;


	/** This is temporary - at some point this should probably be changed to a List<TraceEntry> (where TraceEntry is a new class) or similar */
	public List<TraceEntry> lines = new ArrayList<TraceEntry>();

	public void filterMethods(TraceMethodFilter f) {
		// TODO Auto-generated method stub

	}

	public void filterField(TraceFieldFilter f) {
		// TODO Auto-generated method stub

	}
}
