package swen302.tracer;

import com.sun.jdi.Field;
import com.sun.jdi.Method;

/**
 * A method or field filter that traces everything.
 */
public class AllFilter implements TraceFieldFilter, TraceMethodFilter {
	@Override
	public boolean isFieldTraced(Field f) {
		return true;
	}
	@Override
	public boolean isMethodTraced(Method m) {
		return true;
	}
}
