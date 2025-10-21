package edu.linkprediction.similarityMetrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

public class Sorensen extends Similarity {

    public Sorensen(List<Threshold> thresholdList) {
        super.name = "Sorensen";
        super.thresholdList = thresholdList;
    }

	public float score(Graph<String, Integer> g, String n1, String n2) {

		Set<String> intersect = new HashSet<String>(g.getNeighbors(n1));
		Collection<String> c2neigh = g.getNeighbors(n2);

		float denominador = intersect.size() + c2neigh.size();
		intersect.retainAll(c2neigh);

		return 2 * intersect.size() / denominador;
	}
}
