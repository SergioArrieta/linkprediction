package edu.linkprediction.similarityMetrics;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

import java.util.List;

public class PreferentialAttachment extends Similarity {

    public PreferentialAttachment(List<Threshold> thresholdList) {
        super.name = "PreferentialAttachment";
        super.thresholdList = thresholdList;
    }

	public float score(Graph<String, Integer> g, String n1, String n2) {
		return g.getNeighbors(n1).size() * g.getNeighbors(n2).size();
	}
}
