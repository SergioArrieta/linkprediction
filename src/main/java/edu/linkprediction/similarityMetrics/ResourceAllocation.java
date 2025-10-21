package edu.linkprediction.similarityMetrics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

public class ResourceAllocation extends Similarity {

    public ResourceAllocation(List<Threshold> thresholdList) {
        super.name = "ResourceAllocation";
        super.thresholdList = thresholdList;
    }

	/*
	 * Similar a AdamicAdar pero sin el log
	 * 
	 * 1/|z| / z in |neigh(x) interseccion neigh(y)|
	 */

	@Override
	public float score(Graph<String, Integer> g, String n1, String n2) {
		double similarity = 0f;
		
		Set<String> intersect = new HashSet<String>(g.getNeighbors(n1)); //retorna un unmodified
		
		intersect.retainAll(g.getNeighbors(n2));
		
		for(String i:intersect)
			similarity += (1.0 / g.getNeighborCount(i));
			
		return (float)similarity;
	}
	
}