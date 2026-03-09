package edu.linkprediction.similarityMetrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

public class SimRank extends Similarity {

	/*
	 * SimRank [10] is a fixed point of the following recursive definition: two
	 * nodes are similar to the extent that they are joined to similar neighbors.
	 * 
	 * gamma * sum (similarity entre todos los pares de vecinos de x e y) / (
	 * |neigh(x)| + |neigh(y)| )
	 * 
	 * [10] Glen Jeh and Jennifer Widom. SimRank: A measure of structural-context
	 * similarity. In Proceedings of the ACM SIGKDD International Conference on
	 * Knowledge Discovery and Data Mining, July 2002. considera sim(x,x) = 1
	 * 
	 * http://qualityranking.googlecode.com/svn/trunk/QualityRanking/src/pt/tumba/
	 * links/SimRank.java Esta hecho con los in, no se si no debiera ser con todos
	 */

	float gamma; // dumping
	double[][] similarityMatrix;
	Map<String, Integer> mappingClases;

	public SimRank(Graph<String, Integer> graph, List<Threshold> thresholdList) {
		gamma = 0.6f; // 0.6 sugerencia papers
		mappingClases = new HashMap<String, Integer>();
		similarityMatrix = computeSimilarities(graph);
        super.name = "SimRank";
        super.thresholdList = thresholdList;
	}

	private double[][] computeSimilarities(Graph<String, Integer> graph) {

		int n = graph.getVertexCount();
		int iter = ((int) Math.abs(Math.log((double) n) / Math.log((double) 10))) + 1;
		if (iter < 5) // 5 es la sugerencia del paper original
			iter = 5;
		double[][] scores = new double[n][n];

		List<String> vertices = new ArrayList<String>(graph.getVertices());
		for (String v : vertices) {
			mappingClases.put(v, mappingClases.size());
		}

		for (int i = 0; i < iter; i++) {

			for (int id1 = 0; id1 < vertices.size(); id1++) { // por todos los vertices...

				Collection<String> map1 = graph.getNeighbors(vertices.get(id1)); // in links de id1
				int numInLinks1 = map1.size();

				for (int id2 = 0; id2 < id1; id2++) { // para todos los anteriores (recorre solo la mitad de la matrix)

					Collection<String> map2 = graph.getNeighbors(vertices.get(id2)); // in links de id2
					int numInLinks2 = map2.size();

					double score = 0;

					for (String it1 : map1) {

						for (String it2 : map2) {

							if (it1.equals(it2))
								score += 1;
							else
								score += scores[mappingClases.get(it1)][mappingClases.get(it2)];

						}
					}

					if (numInLinks1 > 0 && numInLinks2 > 0) {
						// System.out.println(vertices.get(id1)+" "+vertices.get(id2)+" "+score+" "+gamma*score /( numInLinks1 + numInLinks2 ));
						scores[id1][id2] = gamma * score / (numInLinks1 + numInLinks2);
					}

				}

			}
		}

		return scores;
	}

	@Override
	public float score(Graph<String, Integer> g, String n1, String n2) {
		try {
			return (float) similarityMatrix[mappingClases.get(n1)][mappingClases.get(n2)];
		} catch (Exception e) {
			System.out.println("null simrank");
			return 0;
		}
	}

}