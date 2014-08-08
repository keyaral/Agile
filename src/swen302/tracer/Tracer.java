package swen302.tracer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.VMDeathRequest;


class TracerMain {
	public static void main(String[] commandLineArgs) throws Exception {
		if(commandLineArgs.length != 3) {
			System.err.println("Requires 3 arguments:");
			System.err.println(" 1. VM options (remember to quote the entire string)");
			System.err.println(" 2. Main class name");
			System.err.println(" 3. Filter regex");
			System.exit(1);
		}

		System.out.println("Trace: ");
		System.out.println(Tracer.Trace(commandLineArgs[0], commandLineArgs[1], new RegexTraceMethodFilter(commandLineArgs[2]), new DefaultFieldFilter()));
	}
}

class ObjectReferenceGenerator {
	private Map<Object, String> map = new HashMap<>();

	public String get(Object obj) {
		return map.get(obj);
	}

	private int nextID = 0;
	public void put(Object obj) {
		map.put(obj, "REF"+String.valueOf(nextID++));
	}
}

public class Tracer {

	/**
	 * Generates a Trace of a given application.
	 *
	 * @param vmOptions The arguments passed into the Virtual Machine
	 * @param mainClass The Main class of the given application
	 * @param methodFilter The method filter to use
	 * @param fieldFilter The field filter to use
	 * @return A string representation of the program trace
	 * @throws Exception This becomes your problem if thrown
	 */
	public static Trace Trace(String vmOptions, String mainClass, TraceMethodFilter methodFilter, TraceFieldFilter fieldFilter) throws Exception
	{
		Trace t = new Trace();

		VirtualMachine vm = launchTracee(mainClass, vmOptions);


		// When a method is entered, send an event to this tracer and suspend the thread that entered it
		MethodEntryRequest entryRequest = vm.eventRequestManager().createMethodEntryRequest();
		entryRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		entryRequest.enable();

		// When a method is exited, send an event to this tracer
		MethodExitRequest exitRequest = vm.eventRequestManager().createMethodExitRequest();
		exitRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		exitRequest.enable();

		VMDeathRequest deathRequest = vm.eventRequestManager().createVMDeathRequest();

		deathRequest.enable();


		// Resume the program (AFTER setting up event requests)
		vm.resume();


		while(true) {
			EventSet events = vm.eventQueue().remove();
			for(Event event : events) {
				if(event instanceof MethodEntryEvent) {
					MethodEntryEvent event2 = (MethodEntryEvent)event;

					if(methodFilter.isMethodTraced(event2.method())) {

						// Handle a method entry
						StackFrame frame = event2.thread().frame(0);
						ObjectReference _this = frame.thisObject();

						if(_this == null)
							t.lines.add("staticContext");
						else
							t.lines.add("objectState "+valueToStateString(fieldFilter, _this, new ObjectReferenceGenerator()));

						t.lines.add("methodCall "+getMethodNameInTraceFormat(event2.method()));

						/*try {
							for(Value v : frame.getArgumentValues()) {
								System.out.println("   argument: "+v);
							}
							if(_this != null && _this.type() instanceof ClassType) {
								for(Field f : ((ClassType)_this.type()).allFields()) {
									System.out.println("   field "+f.name()+": "+_this.getValue(f));
								}
							}
						} catch(InternalException e) {
							// Java bug; InternalException is thrown if getting arguments from a native method
							// see http://bugs.java.com/view_bug.do?bug_id=6810565
							//System.out.println("   (unable to get arguments)");
						}*/
					}

					event2.thread().resume();

				} else if(event instanceof MethodExitEvent) {

					// Handle a method return
					MethodExitEvent event2 = (MethodExitEvent)event;

					if(methodFilter.isMethodTraced(event2.method())) {
						StackFrame frame = event2.thread().frame(0);
						ObjectReference _this = frame.thisObject();

						if(_this == null)
							t.lines.add("staticContext");
						else
							t.lines.add("objectState "+valueToStateString(fieldFilter, _this, new ObjectReferenceGenerator()));

						t.lines.add("return "+getMethodNameInTraceFormat(event2.method()));
					}

					event2.thread().resume();

				}
				else if(event instanceof VMDeathEvent)
				{
					return t;
				}
			}
		}
	}

	/**
	 * Returns a string containing the relevant state of an object, in some human-readable format.
	 */
	private static String objectToStateString(TraceFieldFilter filter, ObjectReference object, ObjectReferenceGenerator refs) throws Exception {

		// Deal with circular references
		{
			String ref = refs.get(object);
			if(ref != null)
				return ref;
			refs.put(object);
		}


		Type type = object.type();
		if(type instanceof ClassType) {

			if(((ClassType)object.type()).isEnum()) {
				boolean fullyInitialized = true;
				for(Field f : ((ClassType)object.type()).allFields()) {
					if(f.isEnumConstant()) {
						Value value = object.getValue(f);
						if(value != null && object.getValue(f).equals(object))
							return f.name();
						if(value == null)
							fullyInitialized = false;
					}
				}
				if(!fullyInitialized)
					return "<uninitialized-enum>";
				throw new AssertionError("failed to find enum constant name");
			}

			if(((ClassType)object.type()).name().equals("java.lang.String")) {
				return "<string>";
			}

			List<Field> fields = ((ClassType)object.type()).allFields();

			StringBuilder result = new StringBuilder();
			//result.append(object.type().name());
			result.append('{');

			boolean first = true;
			for(int k = 0; k < fields.size(); k++) {

				Field f = fields.get(k);

				if(!filter.isFieldTraced(f))
					continue;

				if(!first) result.append(",");
				else first = false;

				result.append(f.name());
				result.append('=');
				result.append(valueToStateString(filter, object.getValue(f), refs));
			}

			result.append('}');

			return result.toString();

		} else if(type instanceof ArrayType) {
			List<Value> values = ((ArrayReference)object).getValues();

			StringBuilder result = new StringBuilder();
			result.append('[');
			boolean first = true;
			for(Value v : values) {
				if(!first) result.append(',');
				else first = false;
				result.append(valueToStateString(filter, v, refs));
			}
			result.append(']');
			return result.toString();

		} else
			throw new AssertionError("Unsupported type "+type.name());
	}

	/**
	 * Returns a string containing the relevant state of any value, in some human-readable format.
	 */
	private static String valueToStateString(TraceFieldFilter filter, Value value, ObjectReferenceGenerator refs) throws Exception {
		if(value == null)
			return "null";
		if(value instanceof ObjectReference)
			return objectToStateString(filter, (ObjectReference)value, refs);
		return value.toString();
	}

	private static String getMethodNameInTraceFormat(Method m) {
		StringBuilder result = new StringBuilder();
		result.append(m.declaringType().name());
		result.append(' ');
		result.append(m.name());
		result.append('(');

		List<String> argTypeNames = m.argumentTypeNames();
		for(int k = 0; k < argTypeNames.size(); k++) {
			if(k > 0) result.append(',');
			result.append(argTypeNames.get(k));
		}

		result.append(')');
		return result.toString();
	}

	private static VirtualMachine launchTracee(String mainClass, String jvmOptions) throws Exception {

		// Find the command-line LaunchingConnector
		LaunchingConnector processConnector = null;
		for(LaunchingConnector ac : Bootstrap.virtualMachineManager().launchingConnectors()) {
			if(ac.name().equals("com.sun.jdi.CommandLineLaunch")) {
				processConnector = ac;
				break;
			}
		}
		if(processConnector == null)
			throw new Exception("didn't find CommandLineLaunch connector");


		// Launch the program, initially suspended
		// Possible Java bug: with suspend=true, processConnector.launch throws an exception
		Map<String, Connector.Argument> args = processConnector.defaultArguments();
		args.get("main").setValue(mainClass);
		args.get("options").setValue(jvmOptions);
		args.get("suspend").setValue("true");
		return processConnector.launch(args);
	}
}
