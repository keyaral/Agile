package swen302.tracer;

import java.util.regex.Pattern;

import com.sun.jdi.Method;

/**
 * Matches method names (of the form <var>pkg.Class.method</var>) against a regex.
 * Method names matching the regex are included.
 */
public class RegexTraceMethodFilter implements TraceMethodFilter {
	private final Pattern pattern;

	public RegexTraceMethodFilter(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	@Override
	public boolean isMethodTraced(Method m) {
		return pattern.matcher(m.declaringType().name()+"."+m.name()).matches();
	}
}
