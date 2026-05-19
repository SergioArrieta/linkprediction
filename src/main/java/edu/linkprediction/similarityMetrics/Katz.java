package edu.linkprediction.similarityMetrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.linkprediction.matrix.Matrix;
import edu.linkprediction.threshold.Threshold;
import edu.uci.ics.jung.graph.Graph;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Katz extends Similarity {
    private double[][] similarityMatrix;
    private Map<String, Integer> mappingClases;

    public Katz(List<Threshold> thresholdList) {
        super.name = "Katz";
        super.thresholdList = thresholdList;
    }

    // synchronized para que el primer hilo que llegue calcule la matriz para ser reusada desp
    private synchronized void prepareMatrix(Graph<String, Integer> graph) {
        if (similarityMatrix != null) {
            return;
        }

        log.info("Calculando matriz de Katz");
        this.mappingClases = new HashMap<>();

        // mapeo de clases
        int i = 0;
        for (String v : graph.getVertices()) {
            mappingClases.put(v, i++);
        }

        // matriz
        Matrix adjacency = computeAdjacency(graph);
        Matrix identity = Matrix.identity(graph.getVertexCount(), graph.getVertexCount());
        double alpha = getAlpha(adjacency);

        Matrix result = identity.minus(adjacency.times(alpha)).inverse().minus(identity);
        this.similarityMatrix = result.getArray();
        log.info("Matriz de Katz calculada exitosamente.");
    }

    private Matrix computeAdjacency(Graph<String, Integer> graph) {
        int size = graph.getVertexCount();
        Matrix A = new Matrix(size, size);
        double[][] AA = A.getArray();

        for (String v : graph.getVertices()) {
            Integer mappingV = mappingClases.get(v);
            Collection<String> out = graph.getSuccessors(v);
            if (out != null) {
                for (String o : out) {
                    Integer mappingO = mappingClases.get(o);
                    if (mappingO != null) { // Seguridad para evitar IndexOutOfBounds
                        AA[mappingV][mappingO] = 1;
                    }
                }
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

    @Override
    public float score(Graph<String, Integer> g, String c1, String c2) {
        if (similarityMatrix == null) {
            prepareMatrix(g);
        }

        Integer idx1 = mappingClases.get(c1);
        Integer idx2 = mappingClases.get(c2);

        if (idx1 == null || idx2 == null) return 0.0f;

        float v = (float) similarityMatrix[idx1][idx2];
        return Float.isNaN(v) ? 0.0f : v;
    }
}