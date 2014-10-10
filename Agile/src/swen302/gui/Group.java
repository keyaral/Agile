package swen302.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import swen302.tracer.FieldKey;

public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<FieldKey> fields = new ArrayList<FieldKey>();
}
