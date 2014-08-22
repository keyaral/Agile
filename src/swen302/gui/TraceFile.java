package swen302.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import swen302.tracer.Trace;

public class TraceFile {

	public Trace[] traces;
	public TracerConfiguration config;

	private static String encodeBytesAsString(byte[] data) {
		StringBuilder result = new StringBuilder();
		for(byte b : data) {
			result.append((char)(((b >> 4) & 15) + 'A'));
			result.append((char)((b & 15) + 'A'));
		}
		return result.toString();
	}

	private static byte[] decodeBytesFromString(String data) throws IOException {
		if((data.length() % 2) != 0)
			throw new IOException("odd number of bytes given");
		byte[] result = new byte[data.length() / 2];
		for(int k = 0; k < result.length; k++) {
			int first = data.charAt(k*2) - 'A';
			int second = data.charAt(k*2+1) - 'A';
			result[k] = (byte)((first << 4) | second);
		}
		return result;
	}

	/**
	 * Saves a multi-tracefile.
	 * @param f The file to save to.
	 * @throws IOException
	 */
	public void write(File f) throws IOException {
		try (FileWriter out = new FileWriter(f)) {
			out.write(encodeBytesAsString(config.writeToByteArray())+"\n");
			for(Trace t : traces) {
				out.write("newTrace\n");
				for(String l : t.lines)
					out.write(l+"\n");
			}
		}
	}

	/**
	 * Loads a multi-tracefile.
	 * @param f The file to load.
	 * @throws IOException
	 */
	public void read(File f) throws IOException {
		List<Trace> result = new ArrayList<Trace>();
		Trace currentTrace = null;
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {

			// save configuration
			{
				String line = br.readLine();
				if(line == null)
					throw new IOException("Corrupted trace file (no config data)");
				config = TracerConfiguration.loadFromByteArray(decodeBytesFromString(line));
			}

			while(true) {
				String line = br.readLine();
				if(line == null)
					break;

				if(line.equals("newTrace")) {
					currentTrace = new Trace();
					result.add(currentTrace);
				} else {
					if(currentTrace == null)
						throw new IOException("invalid trace file");
					currentTrace.lines.add(line);
				}
			}
		}
		traces = result.toArray(new Trace[result.size()]);
	}
}
