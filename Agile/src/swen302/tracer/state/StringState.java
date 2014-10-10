package swen302.tracer.state;


public class StringState extends State {
	private static final long serialVersionUID = 1L;

	public final String stringValue;

	public StringState(String value) {
		this.stringValue = value;
	}

	@Override
	public String toString() {
		return "\""+stringValue.replace("\"","\\\"")+"\"";
	}

	@Override
	public int hashCode() {
		return stringValue.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof StringState && ((StringState)obj).stringValue.equals(stringValue);
	}
}
