package swen302.tracer.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import swen302.tracer.FieldKey;
import swen302.tracer.TraceFilter;

public class ObjectState extends State {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private String className;
	public ObjectState(String className) {
		this.className = className;
	}

	public Map<FieldKey, State> fields = new HashMap<>();

	@Override
	public String toString() {
		return toString(new IdentityHashMap<State, String>());
	}

	public String toString(Map<State, String> alreadySeenObjects) {
		if(alreadySeenObjects.containsKey(this))
			return alreadySeenObjects.get(this);
		alreadySeenObjects.put(this, "OBJ"+alreadySeenObjects.size());

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
			result.append(value.toString(alreadySeenObjects));
		}
		result.append('}');

		return result.toString();
	}

	@Override
	public void filterFields(TraceFilter f) {
		Iterator<Map.Entry<FieldKey, State>> it = fields.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<FieldKey, State> entry = it.next();
			if(!f.isFieldTraced(entry.getKey()))
				it.remove();
			else
				entry.getValue().filterFields(f);
		}
	}

	@Override
	public int hashCode() {
		return 0; // TODO
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ObjectState && ((ObjectState)obj).fields.equals(fields);
	}

	public String getClassName() {
		return className;
	}

}
