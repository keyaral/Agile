package swen302.automaton;

import java.util.List;

import swen302.graph.LabelFormatOptions;
import swen302.tracer.state.State;

public class AutomatonGraphUtils {

	/**
	 * Converts a long method name to a human-readable string.
	 * @param longMethodName A method name in the format "pkg.classname methodname(argtype1,argtype2,argtype3)" (as it appears in a trace file)
	 * @return The short human-readable method name.
	 */
	public static String formatMethodLabel(String longMethodName, List<State> arguments) {

		try{
			String packageAndClass = longMethodName.split(" ")[0];
			String methodAndArgs = longMethodName.split(" ")[1];

			String className = packageAndClass;
			if(className.contains("."))
				className = className.substring(className.lastIndexOf('.')+1);

			if(className.contains("$"))
				className = className.substring(className.lastIndexOf('$')+1);

			String methodName = methodAndArgs.split("\\(")[0];

			String args = "";
			String[] split = methodAndArgs.split("\\(");
			for(int i=1; i<split.length; i++){
				args+="("+split[i];
			}

			String toReturn = "";
			toReturn += LabelFormatOptions.displayClass?className+(LabelFormatOptions.displayMethod?".":" "):"";
			toReturn += LabelFormatOptions.displayMethod?methodName:"";
			toReturn += LabelFormatOptions.displayParams?args:"";

			return toReturn.trim();
		}catch(ArrayIndexOutOfBoundsException e){
			return longMethodName;
		}
	}

	public static String formatMethodLabel(final String longMethodName) {
		return formatMethodLabel(longMethodName, null);
	}


	public static Object createMethodLabelObject(final String longMethodName) {
		return createMethodLabelObject(longMethodName, null);
	}

	public static Object createMethodLabelObject(final String longMethodName, final List<State> arguments) {
		return new Object() {
			@Override
			public String toString() {
				return formatMethodLabel(longMethodName, arguments);
			}
		};
	}

}