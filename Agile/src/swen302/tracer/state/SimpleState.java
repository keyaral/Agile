package swen302.tracer.state;

public class SimpleState extends State {
	private static final long serialVersionUID = 1L;

	private String stringValue;
	public SimpleState(String stringValue) {
		this.stringValue = stringValue;
	}
	@Override
	public String toString() {
		return stringValue;
	}

	@Override
	public int hashCode() {
		return stringValue.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SimpleState && ((SimpleState)obj).stringValue.equals(stringValue);
	}
}
