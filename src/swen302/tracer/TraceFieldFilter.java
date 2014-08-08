package swen302.tracer;

import com.sun.jdi.Field;


/**
 * A filter that specifies which fields to include in the trace.
 *
 * @author campbealex2
 */
public interface TraceFieldFilter {
	/**
	 * Returns whether a field's value should be recorded.
	 * @param f The field.
	 * @return True if values of <var>f</var> should appear in the trace.
	 */
	public boolean isFieldTraced(Field f);
}
