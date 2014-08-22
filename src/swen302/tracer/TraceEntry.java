package swen302.tracer;

import java.io.Serializable;

import com.sun.jdi.Method;

public class TraceEntry implements Serializable{
	private static final long serialVersionUID = 1L;

	public boolean isReturn;

	/** state string, null if static */
	public String state;

	public String declaringClass;
	public String methodName;
	public String[] argumentTypes;

	public TraceEntry() {
	}

	public void setMethod(Method m) {
		declaringClass = m.declaringType().name();
		methodName = m.name();
		argumentTypes = m.argumentTypeNames().toArray(new String[0]);
	}

	/**
	 * Returns the method name and parameters in the format: packagename.ClassName methodName(p1type,p2type,p3type)
	 */
	public String getLongMethodName() {
		StringBuilder sb = new StringBuilder();
		sb.append(declaringClass);
		sb.append(' ');
		sb.append(methodName);
		sb.append('(');
		for(String s : argumentTypes) {
			sb.append(s);
			sb.append(',');
		}
		if(argumentTypes.length > 0)
			sb.setLength(sb.length() - 1);
		sb.append(')');
		return sb.toString();
	}
}
