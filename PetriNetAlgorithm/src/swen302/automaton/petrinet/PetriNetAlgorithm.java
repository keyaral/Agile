package swen302.automaton.petrinet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import swen302.automaton.AutomatonGraphUtils;
import swen302.automaton.VisualizationAlgorithm;
import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.graph.PetriTransitionNode;
import swen302.tracer.FieldKey;
import swen302.tracer.MethodKey;
import swen302.tracer.Trace;
import swen302.tracer.TraceEntry;
import swen302.tracer.state.ObjectState;
import swen302.tracer.state.State;

public class PetriNetAlgorithm implements VisualizationAlgorithm {

	private Graph graph;
	private Map<FieldValueKey, Node> nodes;
	private int nextNodeID;

	private Node getFieldNode(final FieldKey field, final State state) {
		FieldValueKey fvk = new FieldValueKey(field, state);
		Node node = nodes.get(fvk);
		if(node != null)
			return node;

		node = new Node(String.valueOf(nextNodeID++));
		node.setLabel(fvk);

		nodes.put(fvk, node);
		graph.addNode(node);
		return node;
	}

	class TransitionKey {
		MethodKey method;
		Map<FieldKey, State> beforeValues = new HashMap<FieldKey, State>();
		Map<FieldKey, State> afterValues = new HashMap<FieldKey, State>();

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TransitionKey)) return false;

			TransitionKey o = (TransitionKey)obj;
			return method.equals(o.method) && beforeValues.equals(o.beforeValues) && afterValues.equals(o.afterValues);
		}

		@Override
		public int hashCode() {
			return ((method.hashCode()*37 + beforeValues.hashCode()) * 37 + afterValues.hashCode());
		}
	}

	private Set<TransitionKey> seenTransitions = new HashSet<TransitionKey>();

	private void processTransition(State before_, State after_, MethodKey method, String longMethodName, List<State> arguments) {
		if(before_ instanceof ObjectState && after_ instanceof ObjectState) {
			ObjectState before = (ObjectState)before_;
			ObjectState after = (ObjectState)after_;

			// avoid duplicates
			TransitionKey key = new TransitionKey();
			key.method = method;

			for(FieldKey field : before.fields.keySet()) {
				State beforeValue = before.fields.get(field);
				State afterValue = after.fields.get(field);
				if(!beforeValue.equals(afterValue)) {
					key.beforeValues.put(field, beforeValue);
					key.afterValues.put(field, afterValue);
				}
			}

			if(!seenTransitions.add(key))
				return; // already added an identical transition

			Node transition = new PetriTransitionNode(String.valueOf(nextNodeID++));
			transition.setLabel(AutomatonGraphUtils.createMethodLabelObject(longMethodName, arguments));
			graph.addNode(transition);

			for(FieldKey field : key.beforeValues.keySet()) {
				//State beforeValue = before.fields.get(field);
				//State afterValue = after.fields.get(field);
				graph.addEdge(new Edge(String.valueOf(nextNodeID++), "", getFieldNode(field, before.fields.get(field)), transition));
				graph.addEdge(new Edge(String.valueOf(nextNodeID++), "", transition, getFieldNode(field, after.fields.get(field))));
			}
		}
	}

	@Override
	public Graph[] generateGraph(Trace[] trace) {
		graph = new Graph();
		nodes = new HashMap<>();
		nextNodeID = 0;

		for(Trace t : trace) {
			Stack<TraceEntry> stack = new Stack<>();
			for(TraceEntry entry : t.lines) {
				stack.add(entry);

				if(entry.isReturn) {
					TraceEntry after = stack.pop();
					TraceEntry before = stack.pop();

					processTransition(before.state, after.state, entry.method, entry.getLongMethodName(), before.arguments);
				}
			}
		}

		return new Graph[] {graph};
	}

	@Override
	public String toString() {
		return "Petri Net Algorithm";
	}

}

