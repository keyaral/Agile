package swen302.tracer;


/**
 * A method or field filter that traces everything.
 */
public class AllFilter implements TraceFieldFilter, TraceMethodFilter {
	@Override
	public boolean isFieldTraced(FieldKey f) {
		return true;
	}
	@Override
	public boolean isMethodTraced(MethodKey m) {
		return true;
	}
}
