package swen302.tracer.state;

public class NullState extends State {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public int hashCode() {
		return 0x1274742F;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof NullState;
	}
}
