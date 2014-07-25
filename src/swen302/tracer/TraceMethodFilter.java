package swen302.tracer;

import com.sun.jdi.Method;

public interface TraceMethodFilter {
	/**
	 * Returns whether a method should be traced.
	 * @param m The method being called.
	 * @return True if calls to <var>m</var> should appear in the trace.
	 */
	public boolean isMethodTraced(Method m);
}
