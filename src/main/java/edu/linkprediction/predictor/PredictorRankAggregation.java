package edu.linkprediction.predictor;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.ranking.RankAggregation;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.ranking.RankingIndividual;
import edu.linkprediction.similarityMetrics.Similarity;
import edu.linkprediction.threshold.Threshold;
import edu.linkprediction.utils.Utils;
import edu.linkprediction.validation.MicroAvgValidator;
import edu.uci.ics.jung.graph.Graph;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class PredictorRankAggregation { //Esto deberia extender de los otros predictor

    /**
     * Genera todas las dependencias posibles tieniendo en cuenta si existen en la version siguiente. Es decir, solo
     * genera dependencias que pueden ser predichas.
     * @param graph1
     * @param graph2
     * @param sim
     */
    private List<Dependency> generateDependeciesWithScoreByGraph(Graph<String, Integer> graph1, Graph<String, Integer> graph2, Similarity sim) {
        List<Dependency> newDependencies = new ArrayList<>();
        graph1.getVertices().forEach(node -> {
            if (graph2.containsVertex(node)) {
                newDependencies.addAll(calculateScoreByNode(graph1,graph2,node,sim));
            }
        });
        return newDependencies;
    }

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

    public List<String[]> generateFullPrediction(Graph<String, Integer> graphV1, Graph<String, Integer> graphV2,
                                                 Similarity sim1, Similarity sim2, RankAggregation ranking, Threshold threshold) {
        List<String[]> body = new ArrayList<>();
        MicroAvgValidator validator = new MicroAvgValidator();

        List<Dependency> list1 = generateDependeciesWithScoreByGraph(graphV1, graphV2, sim1);
        List<Dependency> list2 = generateDependeciesWithScoreByGraph(graphV1, graphV2, sim2);

        Ranking rankSimple = new RankingIndividual();

        List<Dependency> dependenciesWithScoreRankeadas = ranking.rank(rankSimple.rank(list1),rankSimple.rank(list2));
        log.info("Rank {}",ranking.getName());
        dependenciesWithScoreRankeadas.forEach(dependency -> log.info("Dependencia: {} Score: {}", dependency.toString(), dependency.getScore()));

        body.add(Utils.getRow(validator.getStats(graphV2, threshold.getListFromThreshold(dependenciesWithScoreRankeadas), graphV1),
                sim1.getName() + " " + sim2.getName(),
                            threshold.getName()));

        return body;
    }
}
