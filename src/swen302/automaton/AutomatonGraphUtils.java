package swen302.automaton;

import swen302.graph.GraphSaver;

public class AutomatonGraphUtils {

	/**
	 * Converts a long method name to a human-readable string.
	 * @param longMethodName A method name in the format "pkg.classname methodname(argtype1,argtype2,argtype3)" (as it appears in a trace file)
	 * @return The short human-readable method name.
	 */
	public static String formatMethodLabel(String longMethodName) {

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
			toReturn += GraphSaver.displayClass?className+(GraphSaver.displayMethod?".":" "):"";
			toReturn += GraphSaver.displayMethod?methodName:"";
			toReturn += GraphSaver.displayParams?args:"";

			return toReturn.trim();
		}catch(ArrayIndexOutOfBoundsException e){
			return longMethodName;
		}
	}

	public static Object createMethodLabelObject(final String longMethodName) {
		return new Object() {
			@Override
			public String toString() {
				return formatMethodLabel(longMethodName);
			}
		};
	}

}