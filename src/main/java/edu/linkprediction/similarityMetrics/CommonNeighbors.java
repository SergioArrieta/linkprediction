package edu.linkprediction.similarityMetrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

public class CommonNeighbors extends Similarity {

    public CommonNeighbors(List<Threshold> thresholdList) {
        super.name = "CommonNeighbors";
        super.thresholdList = thresholdList;
    }

	public float score(Graph<String, Integer> g, String n1, String n2) {
		Set<String> intersect = new HashSet<String>(g.getNeighbors(n1)); // retorna un unmodified

		Collection<String> n2neigh = g.getNeighbors(n2);
		intersect.retainAll(n2neigh);

		return intersect.size();
	}

}
