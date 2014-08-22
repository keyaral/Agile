package swen302.tracer.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import swen302.tracer.FieldKey;
import swen302.tracer.TraceFieldFilter;

public class ObjectState extends State {
	private static final long serialVersionUID = 1L;

	private String className;
	public ObjectState(String className) {
		this.className = className;
	}

	public Map<FieldKey, State> fields = new HashMap<>();

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append('{');
		List<FieldKey> sortedFields = new ArrayList<FieldKey>(fields.keySet());
		Collections.sort(sortedFields, new Comparator<FieldKey>() {
			@Override
			public int compare(FieldKey o1, FieldKey o2) {
				return o1.name.compareTo(o2.name);
			}
		});

		boolean first = true;
		for(FieldKey fk : sortedFields) {
			if(first) first = false;
			else result.append(',');

			State value = fields.get(fk);
			result.append(fk.name);
			result.append('=');
			result.append(value.toString());
		}
		result.append('}');

		return result.toString();
	}

	@Override
	public void filterFields(TraceFieldFilter f) {
		Iterator<Map.Entry<FieldKey, State>> it = fields.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<FieldKey, State> entry = it.next();
			if(!f.isFieldTraced(entry.getKey()))
				it.remove();
			else
				entry.getValue().filterFields(f);
		}
	}

}
