package swen302.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import swen302.tracer.Trace;

public class TraceFile implements Serializable {
	private static final long serialVersionUID = 1L;

	public Trace[] traces;
	public TracerConfiguration config;

	/**
	 * Saves a multi-tracefile.
	 * @param f The file to save to.
	 * @throws IOException
	 */
	public void write(File f) throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)))) {
			oos.writeObject(this);
		}
	}

	/**
	 * Loads a multi-tracefile.
	 * @param f The file to load.
	 * @throws IOException
	 */
	public static TraceFile read(File f) throws IOException {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)))) {
			return (TraceFile)ois.readObject();
		} catch(ClassNotFoundException | ClassCastException e) {
			throw new IOException("corrupted trace file?", e);
		}
	}
}
