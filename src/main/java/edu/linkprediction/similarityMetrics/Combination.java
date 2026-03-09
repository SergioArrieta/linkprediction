package edu.linkprediction.similarityMetrics;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class Combination extends Similarity {

    private final Similarity sim1;
    private final Similarity sim2;
    private final float valueP;
    private final float valueK;

    public Combination(List<Threshold> thresholdList, Similarity sim1, Similarity sim2, double valueP, double valueK) {
        this.sim1 = sim1;
        this.sim2 = sim2;
        this.valueP = (float) valueP;
        this.valueK = (float) valueK;
        super.name = "Combination: " + sim1.getName() + " - "+sim2.getName();
        super.thresholdList = thresholdList;
    }

    /**
     * Combina 2 algoritmos de LP para calcular un nuevo score con un valor de ponderacion para cada uno
     */
    public float score(Graph<String, Integer> g, String n1, String n2) {
        return (sim1.score(g, n1, n2) * valueP) + (sim2.score(g, n1, n2) * valueK);
    }
}