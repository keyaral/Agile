package swen302.automaton;

public class AutomatonGraphUtils {

	/**
	 * Converts a long method name to a human-readable string.
	 * @param longMethodName A method name in the format "pkg.classname methodname(argtype1,argtype2,argtype3)" (as it appears in a trace file)
	 * @return The short human-readable method name.
	 */
	public static String formatMethodLabel(String longMethodName) {

		String packageAndClass = longMethodName.split(" ")[0];
		String methodAndArgs = longMethodName.split(" ")[1];

		String className = packageAndClass;
		if(className.contains("."))
			className = className.substring(className.lastIndexOf('.')+1);

		if(className.contains("$"))
			className = className.substring(className.lastIndexOf('$')+1);

		String methodName = methodAndArgs.split("\\(")[0];

		return className+"."+methodName;
	}

}
