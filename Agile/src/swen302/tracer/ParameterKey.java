package swen302.tracer;

import java.io.Serializable;

public final class ParameterKey implements Serializable {
	private static final long serialVersionUID = 1L;

	public final MethodKey method;
	public final int index;

	public ParameterKey(MethodKey method, int index) {
		this.method = method;
		this.index = index;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ParameterKey))
			return false;

		ParameterKey pk = (ParameterKey)obj;
		return pk.method.equals(method) && pk.index == index;
	}

	@Override
	public int hashCode() {
		return method.hashCode() + index*259;
	}

	@Override
	public String toString() {
		return method + " arg" + index;
	}
}
