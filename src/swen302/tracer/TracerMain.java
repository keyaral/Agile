package swen302.tracer;

import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.InternalException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;

public class TracerMain {

	public static void main(String[] commandLineArgs) throws Exception {

		
		
		if(commandLineArgs.length != 3) {
			System.err.println("Requires 2 arguments:");
			System.err.println(" 1. VM options (remember to quote the entire string)");
			System.err.println(" 2. Main class name");
			System.err.println(" 3. Filter regex");
			System.exit(1);
		}

		TraceMethodFilter methodFilter = new RegexTraceMethodFilter(commandLineArgs[2]);

		VirtualMachine vm = launchTracee(commandLineArgs[1], commandLineArgs[0]);


		// When a method is entered, send an event to this tracer and suspend the thread that entered it
		MethodEntryRequest entryRequest = vm.eventRequestManager().createMethodEntryRequest();
		entryRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		entryRequest.enable();

		// When a method is exited, send an event to this tracer
		MethodExitRequest exitRequest = vm.eventRequestManager().createMethodExitRequest();
		exitRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
		exitRequest.enable();


		// Resume the program (AFTER setting up event requests)
		for(ThreadReference thread : vm.allThreads())
			thread.resume();


		while(true) {
			EventSet events = vm.eventQueue().remove();
			for(Event event : events) {
				if(event instanceof MethodEntryEvent) {
					MethodEntryEvent event2 = (MethodEntryEvent)event;

					if(methodFilter.isMethodTraced(event2.method())) {

						// Handle a method entry
						StackFrame frame = event2.thread().frame(0);
						ObjectReference _this = frame.thisObject();

						System.out.println("Intercepted call to "+event2.method()+" on "+(_this == null ? "null" : _this.type().name()));

						try {
							for(Value v : frame.getArgumentValues()) {
								//System.out.println("   argument: "+v);
							}
							if(_this != null && _this.type() instanceof ClassType) {
								for(Field f : ((ClassType)_this.type()).allFields()) {
									//System.out.println("field "+f.name()+": "+_this.getValue(f));
								}
							}
						} catch(InternalException e) {
							// Java bug; InternalException is thrown if getting arguments from a native method
							// see http://bugs.java.com/view_bug.do?bug_id=6810565
							//System.out.println("   (unable to get arguments)");
						}
					}

					event2.thread().resume();

				} else if(event instanceof MethodExitEvent) {

					// Handle a method return
					MethodExitEvent event2 = (MethodExitEvent)event;

					if(methodFilter.isMethodTraced(event2.method())) {
						System.out.println("Call to "+event2.method()+" returned");
					}

				}
			}
		}
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
