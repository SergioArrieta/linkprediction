package edu.linkprediction.similarityMetrics;

import edu.uci.ics.jung.graph.Graph;

public class Combination extends Similarity {

	private Similarity alg1;
	private Similarity alg2;
	private float valueP;
	private float valueK;

	public void setSimilaritiesMeasures(Similarity alg1, Similarity alg2, double d, double e) {
		this.valueK = (float) e;
		this.valueP = (float) d;
		this.alg1 = alg1;
		this.alg2 = alg2;
	}

	/**
	 * Combina 2 algoritmos de LP para calcular un nuevo score con un valor de ponderacion para cada uno
	 */
	public float score(Graph<String, Integer> g, String n1, String n2) {
		return (alg1.score(g, n1, n2) * valueP) + (alg2.score(g, n1, n2) * valueK);
	}
}
