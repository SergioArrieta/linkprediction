package edu.linkprediction.similarityMetrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

public class CommonFollowers extends Similarity {

    public CommonFollowers(List<Threshold> thresholdList) {
        super.name = "CommonFollowers";
        super.thresholdList = thresholdList;
    }

	/**
	 *  o Jaccard Index interseccion / union
	 */
	public float score(Graph<String, Integer> g, String c1, String c2) {
		Set<String> intersect = new HashSet<String>(g.getPredecessors(c1)); //retorna un unmodified
		Set<String> union = new HashSet<String>(g.getPredecessors(c1)); //retorna un unmodified
		
		Collection<String> c2neigh = g.getPredecessors(c2);
		
		intersect.retainAll(c2neigh);
		union.addAll(c2neigh);
		
		float v = intersect.size()/(float)union.size();
		if(Float.isNaN(v))
			v = 0;
		return v;
	
	}
}