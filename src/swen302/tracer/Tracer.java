package swen302.tracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import swen302.tracer.state.ArrayState;
import swen302.tracer.state.EnumState;
import swen302.tracer.state.NullState;
import swen302.tracer.state.ObjectState;
import swen302.tracer.state.SimpleState;
import swen302.tracer.state.State;

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
	 * @param filter The trace filter to use
	 * @param consumer The trace consumer
	 * @return A string representation of the program trace
	 * @throws Exception This becomes your problem if thrown
	 */
	public static void launchAndTraceAsync(String vmOptions, String mainClass, TraceFilter filter, RealtimeTraceConsumer consumer) throws Exception
	{
		VirtualMachine vm = launchTracee(mainClass, vmOptions);

		TraceAsync(vm, filter, consumer);
	}

	/**
	 * Starts tracing the given VM in a separate thread.
	 *
	 * This method traces asynchronously. Trace lines are delivered to the given consumer.
	 *
	 * @param vm The VM to trace.
	 * @param filter The trace filter to use.
	 * @param consumer The consumer that trace lines will be sent to.
	 */
	public static void TraceAsync(final VirtualMachine vm, final TraceFilter filter, final RealtimeTraceConsumer consumer) {
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
									boolean traceable = doesClassHaveTraceableMethods(filter, type);
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

								if(filter.isMethodTraced(new MethodKey(event2.method()))) {

									// Handle a method entry
									StackFrame frame = event2.thread().frame(0);
									ObjectReference _this = frame.thisObject();

									TraceEntry te = new TraceEntry();
									te.method = new MethodKey(event2.method());

									if(_this == null)
										te.state = null;
									else
										te.state = valueToState(filter, _this, new HashMap<ObjectReference, swen302.tracer.state.State>());

									te.isReturn = false;

									// Java bug; InternalException is thrown if getting arguments from a native method
									// see http://bugs.java.com/view_bug.do?bug_id=6810565
									if(!event2.method().isNative()) {
										te.arguments = new ArrayList<>();
										for(Value v : frame.getArgumentValues()) {
											te.arguments.add(valueToState(filter, v, new HashMap<ObjectReference, swen302.tracer.state.State>()));
										}
									}

									consumer.onTraceLine(te);
								}

								threadsToResume.add(event2.thread());

							} else if(event instanceof MethodExitEvent) {

								// Handle a method return
								MethodExitEvent event2 = (MethodExitEvent)event;

								if(filter.isMethodTraced(new MethodKey(event2.method()))) {
									StackFrame frame = event2.thread().frame(0);
									ObjectReference _this = frame.thisObject();

									TraceEntry te = new TraceEntry();
									te.method = new MethodKey(event2.method());

									if(_this == null)
										te.state = null;
									else
										te.state = valueToState(filter, _this, new HashMap<ObjectReference, swen302.tracer.state.State>());

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

	private static boolean doesClassHaveTraceableMethods(TraceFilter filter, ReferenceType type) {
		for(Method m : type.methods())
			if(filter.isMethodTraced(new MethodKey(m)))
				return true;
		return false;
	}

	/**
	 * Returns a string containing the relevant state of an object, in some human-readable format.
	 */
	private static State objectToState(TraceFilter filter, ObjectReference object, Map<ObjectReference, State> alreadySeenObjects) {

		if(alreadySeenObjects.containsKey(object))
			return alreadySeenObjects.get(object);

		Type type = object.type();
		if(type instanceof ClassType) {

			if(((ClassType)object.type()).isEnum()) {
				boolean fullyInitialized = true;
				for(Field f : ((ClassType)object.type()).allFields()) {
					if(f.isEnumConstant()) {
						Value value = object.getValue(f);
						if(value != null && object.getValue(f).equals(object))
							return new EnumState(f.name());
						if(value == null)
							fullyInitialized = false;
					}
				}
				if(!fullyInitialized)
					return new EnumState("<uninitialized-enum>"); // TODO should this be a separate class?
				throw new AssertionError("failed to find enum constant name");
			}

			//if(((ClassType)object.type()).name().equals("java.lang.String")) {
			//	return "<string>";
			//}

			ObjectState state = new ObjectState(object.type().name());
			alreadySeenObjects.put(object, state);

			List<Field> fields = ((ClassType)object.type()).allFields();

			for(int k = 0; k < fields.size(); k++) {

				Field f = fields.get(k);
				FieldKey fk = new FieldKey(f);

				if(!filter.isFieldTraced(fk))
					continue;

				state.fields.put(fk, valueToState(filter, object.getValue(f), alreadySeenObjects));
			}

			return state;

		} else if(type instanceof ArrayType) {

			ArrayState state = new ArrayState();
			alreadySeenObjects.put(object, state);

			List<Value> values = ((ArrayReference)object).getValues();

			for(Value v : values) {
				state.values.add(valueToState(filter, v, alreadySeenObjects));
			}
			return state;

		} else
			throw new AssertionError("Unsupported type "+type.name());
	}

	/**
	 * Returns a string containing the relevant state of any value, in some human-readable format.
	 */
	private static State valueToState(TraceFilter filter, Value value, Map<ObjectReference, State> alreadySeenObjects) {
		if(value == null)
			return new NullState();
		if(value instanceof ObjectReference)
			return objectToState(filter, (ObjectReference)value, alreadySeenObjects);
		return new SimpleState(value.toString());
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
