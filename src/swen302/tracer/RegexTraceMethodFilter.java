package swen302.tracer;

import java.util.regex.Pattern;

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
	public boolean isMethodTraced(MethodKey m) {
		return pattern.matcher(m.className+"."+m.name).matches();
	}
}
