package swen302.tracer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.VMDeathRequest;



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

/**
 * Main interface class for the tracing subsystem.
 */
public class Tracer {

	/**
	 * Starts a program and traces it, asynchronously.
	 * This method returns once the program is running.
	 *
	 * @param vmOptions The arguments passed into the Virtual Machine
	 * @param mainClass The Main class of the given application
	 * @param methodFilter The method filter to use
	 * @param fieldFilter The field filter to use
	 * @param consumer The trace consumer
	 * @return A string representation of the program trace
	 * @throws Exception This becomes your problem if thrown
	 */
	public static void launchAndTraceAsync(String vmOptions, String mainClass, TraceMethodFilter methodFilter, TraceFieldFilter fieldFilter, RealtimeTraceConsumer consumer) throws Exception
	{
		VirtualMachine vm = launchTracee(mainClass, vmOptions);

		TraceAsync(vm, methodFilter, fieldFilter, consumer);
	}

	/**
	 * Starts tracing the given VM in a separate thread.
	 *
	 * This method traces asynchronously. Trace lines are delivered to the given consumer.
	 *
	 * @param vm The VM to trace.
	 * @param methodFilter The method filter to use.
	 * @param fieldFilter The field filter to use.
	 * @param consumer The consumer that trace lines will be sent to.
	 */
	public static void TraceAsync(final VirtualMachine vm, final TraceMethodFilter methodFilter, final TraceFieldFilter fieldFilter, final RealtimeTraceConsumer consumer) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					// class -> does it have any traceable methods?
					Map<ReferenceType, Boolean> knownTraceableClasses = new HashMap<>();

					// When a class is loaded, we need to add a MethodEntryRequest and MethodExitRequest if it's traceable.
					ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
					classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					classPrepareRequest.enable();

					VMDeathRequest deathRequest = vm.eventRequestManager().createVMDeathRequest();

					deathRequest.enable();


					// Resume the program (AFTER setting up event requests)
					vm.resume();


					while(true) {
						EventSet events = vm.eventQueue().remove();
						Set<ThreadReference> threadsToResume = new HashSet<ThreadReference>();
						for(Event event : events) {
							if(event instanceof ClassPrepareEvent) {
								ClassPrepareEvent event2 = (ClassPrepareEvent)event;

								ReferenceType type = event2.referenceType();
								if(!knownTraceableClasses.containsKey(type)) {
									boolean traceable = doesClassHaveTraceableMethods(methodFilter, type);
									knownTraceableClasses.put(type, traceable);
									if(traceable) {
										MethodEntryRequest entryRequest = vm.eventRequestManager().createMethodEntryRequest();
										entryRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
										entryRequest.addClassFilter(type);
										entryRequest.enable();

										// When a method is exited, send an event to this tracer
										MethodExitRequest exitRequest = vm.eventRequestManager().createMethodExitRequest();
										exitRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
										exitRequest.addClassFilter(type);
										exitRequest.enable();
									}
								}

								threadsToResume.add(event2.thread());
							}
							if(event instanceof MethodEntryEvent) {
								MethodEntryEvent event2 = (MethodEntryEvent)event;

								if(methodFilter.isMethodTraced(event2.method())) {

									// Handle a method entry
									StackFrame frame = event2.thread().frame(0);
									ObjectReference _this = frame.thisObject();

									TraceEntry te = new TraceEntry();
									te.setMethod(event2.method());

									if(_this == null)
										te.state = null;
									else
										te.state = valueToStateString(fieldFilter, _this, new ObjectReferenceGenerator());

									te.isReturn = false;
									consumer.onTraceLine(te);

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

								threadsToResume.add(event2.thread());

							} else if(event instanceof MethodExitEvent) {

								// Handle a method return
								MethodExitEvent event2 = (MethodExitEvent)event;

								if(methodFilter.isMethodTraced(event2.method())) {
									StackFrame frame = event2.thread().frame(0);
									ObjectReference _this = frame.thisObject();

									TraceEntry te = new TraceEntry();
									te.setMethod(event2.method());

									if(_this == null)
										te.state = null;
									else
										te.state = valueToStateString(fieldFilter, _this, new ObjectReferenceGenerator());

									te.isReturn = true;
									consumer.onTraceLine(te);
								}

								threadsToResume.add(event2.thread());

							}
							else if(event instanceof VMDeathEvent)
							{
								System.out.println("Tracing done");
								vm.dispose();
								return;
							}
						}
						for(ThreadReference thread : threadsToResume)
							thread.resume();
					}
				} catch(InterruptedException | RuntimeException | IncompatibleThreadStateException | Error t) {
					consumer.onTracerCrash(t);
				} finally {
					consumer.onTraceFinish();
				}
			}
		};

		thread.setName("Tracer thread");
		thread.setDaemon(true);
		thread.start();
	}

	private static boolean doesClassHaveTraceableMethods(TraceMethodFilter methodFilter, ReferenceType type) {
		for(Method m : type.methods())
			if(methodFilter.isMethodTraced(m))
				return true;
		return false;
	}

	/**
	 * Returns a string containing the relevant state of an object, in some human-readable format.
	 */
	private static String objectToStateString(TraceFieldFilter filter, ObjectReference object, ObjectReferenceGenerator refs) {

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
	private static String valueToStateString(TraceFieldFilter filter, Value value, ObjectReferenceGenerator refs) {
		if(value == null)
			return "null";
		if(value instanceof ObjectReference)
			return objectToStateString(filter, (ObjectReference)value, refs);
		return value.toString();
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
