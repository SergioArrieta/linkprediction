package edu.linkprediction.predictor;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.similarityMetrics.Similarity;
import edu.linkprediction.utils.Utils;
import edu.linkprediction.validation.MicroAvgValidator;
import edu.uci.ics.jung.graph.Graph;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Getter
@Slf4j
public class PredictorByGraph extends Predictor {

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

    /**
     * Genera las posibles dependencias por cada nodo pero junta todo en una sola lista y las rankea
     * Devuelve el cuerpo del csv
     * @param graphV1
     * @param graphV2
     * @param similarities
     * @param ranking
     */
    public List<String[]> generateFullPrediction(Graph<String, Integer> graphV1, Graph<String, Integer> graphV2, List<Similarity> similarities, Ranking ranking) {
        List<String[]> body = new ArrayList<>();
        MicroAvgValidator validator = new MicroAvgValidator();

        similarities.forEach(similarity -> {
            // generar TODAS las dependencias para el grafo con el algoritmo de lp similarity
            List<Dependency> dependenciesWithScore = generateDependeciesWithScoreByGraph(graphV1, graphV2, similarity);
            // rankea esas dependencias
            List<Dependency> dependenciesWithScoreRankeadas = ranking.rank(dependenciesWithScore);
            //aplica cada uno de los thresholds
            similarity.getThresholdList().forEach(threshold ->
                    body.add(Utils.getRow(
                            validator.getStats(graphV2, threshold.getListFromThreshold(dependenciesWithScoreRankeadas), graphV1),
                            similarity.getName(),
                            threshold.getName())));
        });
        return body;
    }

}
