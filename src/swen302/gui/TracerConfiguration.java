package swen302.gui;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * All the information that is saved in a saved configuration.
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
}
