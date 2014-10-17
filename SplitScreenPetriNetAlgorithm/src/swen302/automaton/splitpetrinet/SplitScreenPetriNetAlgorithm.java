package swen302.automaton.splitpetrinet;

import swen302.automaton.FieldBasedAlgorithm;
import swen302.automaton.InteractiveVisualizationAlgorithm;
import swen302.automaton.VisualizationAlgorithm;
import swen302.automaton.petrinet.FieldValueKey;
import swen302.automaton.petrinet.PetriNetAlgorithm;
import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.graph.PetriTransitionNode;
import swen302.gui.GraphHoverListener;
import swen302.gui.VertexGraphPane;
import swen302.tracer.Trace;
import swen302.tracer.state.ObjectState;
import swen302.tracer.state.State;

public class SplitScreenPetriNetAlgorithm implements VisualizationAlgorithm, InteractiveVisualizationAlgorithm {

	PetriNetAlgorithm pna = new PetriNetAlgorithm();
	FieldBasedAlgorithm fba = new FieldBasedAlgorithm();

	Graph pnGraph, fbGraph;

	@Override
	public Graph[] generateGraph(Trace[] traces) {

		pnGraph = pna.generateGraph(traces)[0];
		fbGraph = fba.generateGraph(traces)[0];

		return new Graph[] {pnGraph, fbGraph};
	}

	@Override
	public String toString() {
		return "Split-Screen Petri Net Algorithm";
	}

	@Override
	public void setupInteractiveFeatures(VertexGraphPane[] graphPanes) {
		VertexGraphPane pnGraphPane = graphPanes[0];
		VertexGraphPane fbGraphPane = graphPanes[1];

		fbGraphPane.addHoverListener(new GraphHoverListener() {
			@Override
			public void onMouseHover(Node node) {

				if(node == null || !(node.getState() instanceof ObjectState)) {
					for(Node n : pnGraph.nodes)
						n.highlighted = false;
					return;
				}

				ObjectState automatonState = (ObjectState)node.getState();

				for(Node n : pnGraph.nodes)
				{
					if(n instanceof PetriTransitionNode || !(n.getState() instanceof FieldValueKey))
						continue;

					FieldValueKey fvk = (FieldValueKey)n.getState();

					State automatonValue = automatonState.fields.get(fvk.field);
					State petriValue = fvk.value;

					n.highlighted = automatonValue != null && petriValue != null && automatonValue.equals(petriValue);
				}
			}
		});

		pnGraphPane.addHoverListener(new GraphHoverListener() {
			@Override
			public void onMouseHover(Node node) {

				if(node instanceof PetriTransitionNode) {
					for(Node n : fbGraph.nodes)
						n.highlighted = false;
					for(Edge e : fbGraph.edges)
						e.highlighted = e.label.equals(node.getState());
					return;
				}

				for(Edge e : fbGraph.edges)
					e.highlighted = false;

				if(node == null || !(node.getState() instanceof FieldValueKey)) {
					for(Node n : fbGraph.nodes)
						n.highlighted = false;
					return;
				}

				FieldValueKey fvk = (FieldValueKey)node.getState();

				for(Node n : fbGraph.nodes)
				{
					if(!(n.getState() instanceof ObjectState))
						continue;

					ObjectState automatonState = (ObjectState)n.getState();

					State automatonValue = automatonState.fields.get(fvk.field);
					State petriValue = fvk.value;

					n.highlighted = automatonValue != null && petriValue != null && automatonValue.equals(petriValue);
				}
			}
		});
	}

}

