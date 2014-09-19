package swen302.tracer;

import java.io.ObjectInputStream;

import swen302.gui.TraceFile;

public class ConvertTraceToText {
	public static void main(String[] args) throws Exception {
		TraceFile tf;
		try (ObjectInputStream oin = new ObjectInputStream(System.in)) {
			tf = (TraceFile)oin.readObject();
		}
		for(Trace t : tf.traces) {
			for(TraceEntry te : t.lines) {
				if(te.state != null)
					System.out.println("objectState "+te.state);
				System.out.println((te.isReturn ? "return" : "methodCall") + " " + te.method);
			}
		}
	}
}
