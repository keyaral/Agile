package swen302.automaton;

import java.util.List;
import java.util.Objects;

import swen302.graph.LabelFormatOptions;
import swen302.tracer.state.State;

public class AutomatonGraphUtils {

	/**
	 * Converts a long method name to a human-readable string.
	 *
	 * TODO refactor this and related method to take a MethodKey, not a String with a weird format.
	 *
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

			if(LabelFormatOptions.displayParamTypes || LabelFormatOptions.displayParamValues) {
				String[] argtypes = methodAndArgs.split("\\(", 2)[1].split(",");

				for(int k = 0; k < argtypes.length; k++) {
					String argtype = argtypes[k];
					State value = (arguments == null || arguments.size() <= k ? null : arguments.get(k));

					int last$ = argtype.lastIndexOf('$');
					int lastDot = argtype.lastIndexOf('.');
					if(last$ != -1 && lastDot != -1)
						argtype = argtype.substring(Math.max(lastDot, last$)+1);
					else if(last$ != -1)
						argtype = argtype.substring(last$+1);
					else if(lastDot != -1)
						argtype = argtype.substring(lastDot+1);

					if(!LabelFormatOptions.displayParamValues)
						value = null;

					if(!LabelFormatOptions.displayParamTypes && value == null)
						continue;

					if(argtype.endsWith(")"))
						argtype = argtype.substring(0, argtype.length() - 1);

					args += ",";

					if(LabelFormatOptions.displayParamTypes)
						args += argtype;

					if(value != null) {
						if(LabelFormatOptions.displayParamTypes)
							args += " ";
						args += value.toString();
					}
				}

				if(args.length() > 0)
					args = args.substring(1);
			}

			String toReturn = "";
			toReturn += LabelFormatOptions.displayClass?className+(LabelFormatOptions.displayMethod?".":" "):"";
			toReturn += LabelFormatOptions.displayMethod?methodName:"";
			if(LabelFormatOptions.displayParamTypes || LabelFormatOptions.displayParamValues)
				toReturn += "(" + args + ")";

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

	private static class MethodLabelObject {
		private String longMethodName;
		private List<State> arguments;

		@Override
		public String toString() {
			return formatMethodLabel(longMethodName, arguments);
		}

		@Override
		public int hashCode() {
			return 0; // currently unnecessary as this is never stored in a hashset
		}

		@Override
		public boolean equals(Object obj) {
			if(obj.getClass() != getClass())
				return false;
			MethodLabelObject o = (MethodLabelObject)obj;
			return o.longMethodName.equals(longMethodName) && Objects.equals(o.arguments, arguments);
		}
	}
	public static Object createMethodLabelObject(final String longMethodName, final List<State> arguments) {
		MethodLabelObject mlo = new MethodLabelObject();
		mlo.longMethodName = longMethodName;
		mlo.arguments = arguments;
		return mlo;
	}

}