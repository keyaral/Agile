package swen302.tracer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Trace {
	/** This is temporary - at some point this should probably be changed to a List<TraceEntry> (where TraceEntry is a new class) or similar */
	public List<String> lines = new ArrayList<String>();

	public static Trace readFile(String filename) throws IOException {
		Scanner in = new Scanner(new File(filename));
		List<String> lines = new ArrayList<String>();

		while(in.hasNextLine())
			lines.add(in.nextLine());
		in.close();

		Trace t = new Trace();
		t.lines = lines;
		return t;
	}
}
