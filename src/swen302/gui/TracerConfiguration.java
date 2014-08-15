package swen302.gui;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swen302.execution.ExecutionData;

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
}
