package swen302.tracer;



/**
 * A filter that specifies what to include in the trace.
 *
 * @author campbealex2
 */
public interface TraceFilter {
	/**
	 * Returns whether a field's value should be recorded.
	 * @param f The field.
	 * @return True if values of <var>f</var> should appear in the trace.
	 */
	public boolean isFieldTraced(FieldKey f);

	/**
	 * Returns whether a method should be traced.
	 * @param m The method being called.
	 * @return True if calls to <var>m</var> should appear in the trace.
	 */
	public boolean isMethodTraced(MethodKey m);
}
