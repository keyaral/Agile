package swen302.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import swen302.tracer.FieldKey;
import swen302.tracer.TraceEntry;
import swen302.tracer.state.ObjectState;

public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<FieldKey> fields = new ArrayList<FieldKey>();

	private final String groupID;
	private static int nextGroupID;
	private FieldKey fakeFieldKey;

	{
		groupID = "__g"+String.valueOf(nextGroupID++);
		fakeFieldKey = new FieldKey(groupID, groupID);
	}

	public FieldKey getFakeFieldKey() {
		return fakeFieldKey;
	}

	public void updateLine(TraceEntry line) {
		ObjectState os = (ObjectState)line.state;

		// Pretend the group is a sub-object, so turn e.g. {x=1, y=1}
		// into {x=1, y=1, __gID={x=1, y=1}} if x and y are in the group.

		boolean anyFields = false;

		ObjectState groupState = new ObjectState(groupID);
		for(FieldKey field : fields) {
			if(os.getClassName().equals(field.className))
			{
				anyFields = true;
				groupState.fields.put(new FieldKey(fakeFieldKey.className, field.name), os.fields.get(field));
			}
		}

		if(anyFields)
			os.fields.put(fakeFieldKey, groupState);
	}
}
