package edu.linkprediction.similarityMetrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

public class HubDepressed extends Similarity {

    public HubDepressed(List<Threshold> thresholdList) {
        super.name = "HubDepressed";
        super.thresholdList = thresholdList;
    }

	public float score(Graph<String, Integer> g, String n1, String n2) {
		Set<String> intersect = new HashSet<String>(g.getNeighbors(n1)); // retorna un unmodified
		Set<String> union = new HashSet<String>(g.getNeighbors(n1)); // retorna un unmodified

		Collection<String> n2neigh = g.getNeighbors(n2);

		intersect.retainAll(n2neigh);
		union.addAll(n2neigh);

		float cn = intersect.size() / (float) union.size();
		int max = Math.max(g.getNeighbors(n1).size(), g.getNeighbors(n2).size());
		return cn / max;
	}
}
