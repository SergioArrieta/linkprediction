package edu.linkprediction.similarityMetrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.linkprediction.matrix.Matrix;
import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;

public class Katz extends Similarity {

    public Katz(List<Threshold> thresholdList) {
        super.name = "Katz";
        super.thresholdList = thresholdList;
    }
	/**
	 * http://people.cs.vt.edu/badityap/classes/cs6604-Fall13/readings/katz-1953.pdf
	 * A NEW STATUS INDEX DERIVED FROM SOCIOMETRIC ANALYSIS similarity matrix = ( I
	 * - alpha*A )^-1 -I (restar la identidad no tiene sentido?)
	 * 
	 * A = adjacency matrix alpha = measures the non-attenuation of a link. Needs to
	 * be less than the inverse of the spectral radius of A
	 * 
	 * alpha = 0.85 * (spectral radius)^-1 (segun: A novel way of computing
	 * similarities between nodes of a graph, with application to collaborative
	 * recommendation)
	 * 
	 * spectral radious = max{eigenvalues}
	 * 
	 * Este calcula en el constructor... para evitar repetir todos los c�lculos!--
	 * ya no
	 * 
	 **/

	double[][] similarityMatrix;
	Map<String, Integer> mappingClases;

	private double[][] computeSimilarityMatrix(Graph<String, Integer> graph) {

		Matrix adjacency = computeAdjacency(graph);
		Matrix identity = Matrix.identity(graph.getVertexCount(), graph.getVertexCount());
		double alpha = getAlpha(adjacency);
		// adjacency.print(3,2);
		// System.out.println();
		adjacency = adjacency.times(alpha); // alpha*A
		// adjacency.print(3,2);
		// System.out.println();
		adjacency = identity.minus(adjacency); // I - alpha*A
		// adjacency.print(3,2);
		// System.out.println();
		adjacency = adjacency.inverse(); // (I - alpha*A)^1
		// adjacency.print(3,2);
		// System.out.println();
		// adjacency.minus(identity).print(3,2);
		return adjacency.minus(identity).getArray();
	}

	private Matrix computeAdjacency(Graph<String, Integer> graph) {
		Collection<String> n = graph.getVertices();
		Matrix A = new Matrix(n.size(), n.size());
		double[][] AA = A.getArray();
		for (String v : n) // se puede sacar
			mappingClases.put(v, mappingClases.size());

		for (String v : n) {
			int mappingV = mappingClases.get(v);
			Collection<String> out = graph.getSuccessors(v); // No es sim�trico!
			for (String o : out) {
				AA[mappingV][mappingClases.get(o)] = 1; // por ahora no tiene pesos!
			}

		}

		return A;
	}

	private double getAlpha(Matrix A) {
		double[] eigens = A.eig().getRealEigenvalues();
		double spectralRatio = Double.MIN_VALUE;
		for (double d : eigens) {
			double ad = Math.abs(d);
			if (ad > spectralRatio)
				spectralRatio = ad;
		}

		return 0.85 * (1.0 / spectralRatio);
	}

	public float score(Graph<String, Integer> g, String c1, String c2) {

		mappingClases = new HashMap<String, Integer>();
		similarityMatrix = computeSimilarityMatrix(g);

		float v = (float) similarityMatrix[mappingClases.get(c1)][mappingClases.get(c2)];
		if (Float.isNaN(v))
			v = 0;
		return v;
	}
}