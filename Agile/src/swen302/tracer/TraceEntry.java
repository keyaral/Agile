package swen302.tracer;

import java.io.Serializable;
import java.util.List;

import swen302.tracer.state.State;

public class TraceEntry implements Serializable{
	private static final long serialVersionUID = 1L;

	public boolean isReturn;

	/** The state of the object before the method was called (or before it returned).
	 * Null if the method is static. */
	public State state;

	/**
	 * The arguments to the current method call.
	 * Null if the method is native, or if this is a return entry.
	 * Contains nulls in place of parameters that aren't recorded.
	 */
	public List<State> arguments;

	public MethodKey method;


	/**
	 * Returns the method name and parameters in the format: packagename.ClassName methodName(p1type,p2type,p3type)
	 */
	public String getLongMethodName() {
		StringBuilder sb = new StringBuilder();
		sb.append(method.className);
		sb.append(' ');
		sb.append(method.name);
		sb.append('(');
		for(String s : method.argTypes) {
			sb.append(s);
			sb.append(',');
		}
		if(method.argTypes.length > 0)
			sb.setLength(sb.length() - 1);
		sb.append(')');
		return sb.toString();
	}


	public void filterFields(TraceFilter f) {
		state.filterFields(f);
	}
}
