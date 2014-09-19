package swen302.tracer;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * An unambiguous name of a field - that is, a class name and a field name.
 * @author campbealex2
 */
public final class FieldKey implements Serializable {
	private static final long serialVersionUID = 1L;

	public final String className;
	public final String name;

	public FieldKey(String className, String name) {
		if(className == null || name == null)
			throw new NullPointerException();
		this.className = className;
		this.name = name;
	}

	public FieldKey(Field field) {
		this(field.getDeclaringClass().getName(), field.getName());
	}

	public FieldKey(com.sun.jdi.Field field) {
		this(field.declaringType().name(), field.name());
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof FieldKey))
			return false;
		FieldKey fk = (FieldKey)obj;

		if(!fk.name.equals(name))
			return false;

		if(!fk.className.equals(className))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return className.hashCode() ^ name.hashCode();
	}

	@Override
	public String toString() {
		return className + "." + name;
	}
}
