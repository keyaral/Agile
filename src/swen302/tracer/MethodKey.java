package swen302.tracer;

import java.io.Serializable;
import java.lang.reflect.Method;

public final class MethodKey implements Serializable {
	private static final long serialVersionUID = 1L;

	public final String className;
	public final String name;
	public final String[] argTypes;

	public MethodKey(String className, String name, String[] argTypes) {
		if(className == null || name == null)
			throw new NullPointerException();
		this.className = className;
		this.name = name;
		this.argTypes = argTypes;
	}

	public MethodKey(Method method) {
		this(method.getDeclaringClass().getName(), method.getName(), getArgTypesArray(method.getParameterTypes()));
	}

	public MethodKey(com.sun.jdi.Method method) {
		this(method.declaringType().name(), method.name(), method.argumentTypeNames().toArray(new String[0]));
	}

	private static String[] getArgTypesArray(Class<?>[] types) {
		String[] stringTypes = new String[types.length];
		for(int k = 0; k < types.length; k++)
			stringTypes[k] = types[k].getName();
		return stringTypes;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MethodKey))
			return false;
		MethodKey mk = (MethodKey)obj;

		if(!mk.name.equals(name))
			return false;

		if(!mk.className.equals(className))
			return false;

		if(mk.argTypes.length != argTypes.length)
			return false;

		for(int k = 0; k < argTypes.length; k++)
			if(!argTypes[k].equals(mk.argTypes[k]))
				return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (className.hashCode() ^ name.hashCode()) + argTypes.length;
	}

	@Override
	public String toString() {
		return className + "." + name + "(" + getReadableArgs() + ")";
	}

	private String toReadableClassName(String className) {
		if(className.equals("[B")) return "byte[]";
		if(className.equals("[C")) return "char[]";
		if(className.equals("[S")) return "short[]";
		if(className.equals("[I")) return "int[]";
		if(className.equals("[J")) return "long[]";
		if(className.equals("[F")) return "float[]";
		if(className.equals("[D")) return "double[]";
		if(className.equals("[Z")) return "boolean[]";
		if(className.startsWith("[L")) return toReadableClassName(className.substring(2, className.length()-1))+"[]";
		if(className.startsWith("["))
			return toReadableClassName(className.substring(1))+"[]";

		if(className.contains("."))
			className = className.substring(className.lastIndexOf('.') + 1);
		if(className.contains("$"))
			className = className.substring(className.lastIndexOf('$') + 1);
		return className;
	}

	/**
	 * Returns the method's arguments, in human-readable form.
	 */
	public String getReadableArgs() {
		StringBuilder argsString = new StringBuilder();
		for(String argType : argTypes) {
			argsString.append(toReadableClassName(argType));
			argsString.append(", ");
		}
		if(argTypes.length != 0)
			argsString.setLength(argsString.length() - 2);
		return argsString.toString();
	}
}
