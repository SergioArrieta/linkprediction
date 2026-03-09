package edu.linkprediction.predictor;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.similarityMetrics.Similarity;
import edu.uci.ics.jung.graph.Graph;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public abstract class Predictor {

    public abstract List<String[]> generateFullPrediction(Graph<String, Integer> graphV1, Graph<String, Integer> graphV2, List<Similarity> similarities, Ranking ranking);

    protected List<Dependency> calculateScoreByNode(Graph<String, Integer> graph1, Graph<String, Integer> graph2, String vertice, Similarity similarity) {
        List<Dependency> dependenciesList = new ArrayList<>();

        // Si no tiene vecino, currentNeighbors.contains(possibleNeighbor) siempre devuelve true
        Set<String> currentNeighbors = Objects.nonNull(graph1.getSuccessors(vertice)) ? new HashSet<>(graph1.getSuccessors(vertice)) : new HashSet<>();

        graph1.getVertices().forEach(possibleNeighbor -> {
            if (graph2.getVertices().contains(possibleNeighbor)  //el posible vecino tiene que existir en la proxima version
                    && !currentNeighbors.contains(possibleNeighbor) // no debe ser ya vecino
                    && !vertice.equals(possibleNeighbor)) { // no debe ser si mismo
                float value = similarity.score(graph1, vertice, possibleNeighbor);
                log.info("{}: Score for {} -> {}: {}", similarity.getName(), vertice, possibleNeighbor, value);
                if (value > 0) {
                    Dependency dependency = new Dependency(vertice, possibleNeighbor, value);
                    dependenciesList.add(dependency);
                }
            }
        });

        return dependenciesList;
    }
}