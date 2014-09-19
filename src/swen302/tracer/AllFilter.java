package swen302.tracer;


/**
 * A filter that traces everything.
 */
public class AllFilter implements TraceFilter {
	@Override
	public boolean isFieldTraced(FieldKey f) {
		return true;
	}
	@Override
	public boolean isMethodTraced(MethodKey m) {
		return true;
	}
}
