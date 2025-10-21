package edu.linkprediction.similarityMetrics;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class Similarity {

    // Tiene la lista de thresholds porque el parametro de cada uno puede ser distinto dependiendo del algoritmo de LP
    protected List<Threshold> thresholdList;
    protected String name;
	public abstract float score(Graph<String, Integer> g, String n1, String n2);

}