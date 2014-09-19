package swen302.tracer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Trace implements Serializable {
	private static final long serialVersionUID = 1L;


	/** This is temporary - at some point this should probably be changed to a List<TraceEntry> (where TraceEntry is a new class) or similar */
	public List<TraceEntry> lines = new ArrayList<TraceEntry>();

	public void applyFilter(TraceFilter f) {
		Iterator<TraceEntry> it = lines.iterator();
		while(it.hasNext()) {
			if(!f.isMethodTraced(it.next().method))
				it.remove();
		}

		for(TraceEntry te : lines)
			te.filterFields(f);
	}
}
