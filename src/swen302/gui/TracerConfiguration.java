package swen302.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swen302.execution.ExecutionData;
import swen302.tracer.FieldKey;
import swen302.tracer.MethodKey;

/**
 * A saved configuration. A configuration file is just a serialized instance of this.
 *
 * @author Alex Campbell
 */
public class TracerConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	public File jarFile;

	public Map<MethodKey, Boolean> selectedMethods = new HashMap<>();
	public Map<FieldKey, Boolean> selectedFields = new HashMap<>();

	public String algorithmClassName;
	public String algorithmName;

	public List<ExecutionData> executions = new ArrayList<>();

	public boolean displayID,displayState,displayClass,displayMethod,displayParams;

	public boolean continuousUpdating = false;

	public boolean haveGraphPhysicsSettings;
	public double graphElectricStrength;
	public double graphSpringStrength;
	public double graphSpringLength;

	public int k;


	/**
	 * Saves this configuration to a byte array.
	 * The configuration can be re-created using {@link #loadFromByteArray(byte[])}
	 * @return The byte array.
	 */
	public byte[] writeToByteArray() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(this);
			}
			return baos.toByteArray();
		} catch(IOException e) {
			throw new RuntimeException("this shouldn't happen", e);
		}
	}

	/**
	 * Loads a configuration from a byte array previously returned from {@link #writeToByteArray()}
	 * @param data The byte array.
	 * @return The configuration.
	 * @throws IOException If the configuration could not be loaded.
	 */
	public static TracerConfiguration loadFromByteArray(byte[] data) throws IOException {
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
			return (TracerConfiguration)ois.readObject();
		} catch(ClassCastException | ClassNotFoundException e) {
			throw new IOException("corrupted configuration data", e);
		}
	}
}
