package swen302.automaton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {

	public String stateName;
	public float initStateProb;
	public boolean first = false;
	public List<String> methodCalls = new ArrayList<String>();


	public float[] processMethodCallProbs(){
		Map<String,Integer> calls = new HashMap<String, Integer>();
		for(String s : methodCalls){
			if(calls.containsKey(s)){
				calls.put(s, calls.get(s)+1);
			}else{
				calls.put(s, 1);
			}
		}
		float[] probs = new float[HMMAlgorithm.callHierarchy.size()];
		int index = 0;
		for(String s : HMMAlgorithm.callHierarchy){
			if(calls.containsKey(s)){
				probs[index] = (float)calls.get(s)/(float)methodCalls.size();
			}else{
				probs[index] = 0.0f;
			}
			index++;
		}
		return probs;
	}

}
