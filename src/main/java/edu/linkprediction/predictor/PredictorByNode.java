package edu.linkprediction.predictor;

import java.util.*;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.similarityMetrics.Similarity;
import edu.linkprediction.utils.Utils;
import edu.linkprediction.validation.MacroAvgValidator;
import edu.uci.ics.jung.graph.Graph;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

@Getter
@Slf4j
public class PredictorByNode extends Predictor {

    /**
     * Genera todas las dependencias posibles tieniendo en cuenta si existen en la version siguiente. Es decir, solo
     * genera dependencias que pueden ser predichas.
     * Las dependencias son por nodo asi que duelve una lista de listas ya rankeadas
     * @param graph1
     * @param graph2
     * @param ranking
     * @param sim
     */
    private List<List<Dependency>> generateDependeciesWithScoreByNode(Graph<String, Integer> graph1, Graph<String, Integer> graph2,
                                                            Ranking ranking, Similarity sim) {
        List<List<Dependency>> newDependencies = new ArrayList<>();
        graph1.getVertices().forEach(node -> {
            List<Dependency> dependenciesFromNode = calculateScoreByNode(graph1, graph2, node, sim);
            newDependencies.add(ranking.rank(dependenciesFromNode));
        });
        return newDependencies;
    }

    /**
     * Genera las posibles dependencias por cada nodo. Cada lista implica unicamente las dependencias de un solo nodo.
     * Rankea cada lista individualmente y al final mergea los resultados
     * REQUIERE getStatsMacroAverage
     * @param graphV1
     * @param graphV2
     * @param similarities
     * @param ranking
     */
    public List<String[]> generateFullPrediction(Graph<String, Integer> graphV1, Graph<String, Integer> graphV2, List<Similarity> similarities, Ranking ranking) {
        List<String[]> body = new ArrayList<>();
        MacroAvgValidator validator = new MacroAvgValidator();

        similarities.forEach(similarity -> {
            // generar todas la lista de dependencias de cada nodo con el algoritmo de lp similarity dado y ordernarla en base al ranking
            List<List<Dependency>> newDependencies = generateDependeciesWithScoreByNode(graphV1, graphV2, ranking, similarity);
            // recorrer la lista de threholds y aplicar cada uno a cada lista ya ordenada de dependencia
            similarity.getThresholdList().forEach(threshold -> {
                HashMap<String, List<Dependency>> dependenciesByNode = new HashMap<>();
                newDependencies.forEach(dependenciasRankeadsDelnodo -> {
                    if (CollectionUtils.isNotEmpty(dependenciasRankeadsDelnodo)) {
                        dependenciesByNode.put(dependenciasRankeadsDelnodo.get(0).getNodoA(), threshold.getListFromThreshold(dependenciasRankeadsDelnodo));
                    }
                });
                body.add(Utils.getRow(validator.getStats(graphV2, dependenciesByNode, graphV1),similarity.getName(),threshold.getName()));
            });
        });
        return body;
    }

}
