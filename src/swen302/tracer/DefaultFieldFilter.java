package swen302.tracer;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.PrimitiveType;

/**
 * Traces primitive fields only.
 *
 * @author campbealex2
 */
public class DefaultFieldFilter implements TraceFieldFilter {

	@Override
	public boolean isFieldTraced(Field f) {
		try {
			return f.type() instanceof PrimitiveType;
		} catch (ClassNotLoadedException e) {
			return true;
		}
	}

}
