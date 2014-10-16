package swen302.automaton.petrinet;

import swen302.graph.LabelFormatOptions;
import swen302.tracer.FieldKey;
import swen302.tracer.state.State;

public class FieldValueKey {
	public final FieldKey field;
	public final State value;
	public FieldValueKey(FieldKey f, State v) {
		field = f;
		value = v;
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof FieldValueKey))
			return false;
		FieldValueKey o = (FieldValueKey)obj;
		return o.field.equals(field) && o.value.equals(value);
	}
	@Override
	public int hashCode() {
		return field.hashCode() ^ value.hashCode();
	}

	@Override
	public String toString() {
		if(!LabelFormatOptions.displayState)
			return "";
		return (!LabelFormatOptions.displayClass ? field.name : field)+"="+value;
	}
}