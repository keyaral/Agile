package swen302.tracer.state;


public class EnumState extends State {
	private static final long serialVersionUID = 1L;

	private String value;

	public EnumState(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof EnumState && ((EnumState)obj).value.equals(value);
	}
}
