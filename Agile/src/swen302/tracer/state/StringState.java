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
}
