package edu.linkprediction.similarityMetrics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

public class AdamicAdar extends Similarity {

    public AdamicAdar(List<Threshold> thresholdList) {
        super.name = "AdamicAdar";
        super.thresholdList = thresholdList;
    }

	/**
	 * 1/log(|z|) / z in |neigh(x) interseccion neigh(y)|
	 */
	public float score(Graph<String, Integer> g, String c1, String c2) {

		double similarity = 0f;

		Set<String> intersect = new HashSet<String>(g.getNeighbors(c1)); // retorna un unmodified

		intersect.retainAll(g.getNeighbors(c2));

		for (String i : intersect)
			similarity += 1.0 / (Math.log(g.getNeighborCount(i)));

		return (float) similarity;
	}
}
