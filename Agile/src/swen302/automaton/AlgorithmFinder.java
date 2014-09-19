package swen302.automaton;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class AlgorithmFinder {

	private static List<Class<? extends VisualizationAlgorithm>> classes = null;
	public static synchronized List<Class<? extends VisualizationAlgorithm>> getAlgorithmClasses() {
		if(classes == null) {

			classes = new ArrayList<>();

			ServiceLoader<VisualizationAlgorithm> l = ServiceLoader.load(VisualizationAlgorithm.class);

			for(VisualizationAlgorithm a : l) {
				classes.add(a.getClass());
			}
		}

		if(classes.isEmpty())
			throw new RuntimeException("No visualization algorithms found");

		return classes;
	}

}
