package edu.linkprediction.predictor;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.similarityMetrics.Similarity;
import edu.uci.ics.jung.graph.Graph;

import java.util.*;

public abstract class Predictor {

    public abstract List<String[]> generateFullPrediction(Graph<String, Integer> graphV1, Graph<String, Integer> graphV2, List<Similarity> similarities, Ranking ranking);

    protected List<Dependency> getListByNode(Graph<String, Integer> graph, String vertice, Similarity similarity) {
        List<Dependency> dependenciesList = new ArrayList<>();
        if (Objects.nonNull(graph.getNeighbors(vertice))) {
            Set<String> currentNeighbors = new HashSet<String>(graph.getNeighbors(vertice));
            graph.getVertices().forEach(possibleNeighbor -> {
                if (!currentNeighbors.contains(possibleNeighbor) && (!vertice.equals(possibleNeighbor))) {
                    float value = similarity.score(graph, vertice, possibleNeighbor);
                    if (value > 0) {
                        Dependency dependency = new Dependency(vertice, possibleNeighbor, value);
                        dependenciesList.add(dependency);
                    }
                }
            });
        }
        return dependenciesList;
    }
}
