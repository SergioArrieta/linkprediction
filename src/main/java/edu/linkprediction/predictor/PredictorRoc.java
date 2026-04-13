package edu.linkprediction.predictor;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.similarityMetrics.Similarity;
import edu.linkprediction.threshold.Threshold;
import edu.linkprediction.utils.Utils;
import edu.uci.ics.jung.graph.Graph;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class PredictorRoc {

    public List<String[]> generateFullPrediction(Graph<String, Integer> graphV1, Graph<String, Integer> graphV2, List<Similarity> sims, Ranking ranking, Threshold tr) {
        List<String[]> body = new ArrayList<>();
        List<Dependency> dependenciesFromNextVersion = Utils.getDependiciesFromGraph(graphV2);

        graphV1.getVertices().forEach(node -> {
            Set<String> currentNeighbors = Objects.nonNull(graphV1.getSuccessors(node)) ? new HashSet<>(graphV1.getSuccessors(node)) : new HashSet<>();

            graphV1.getVertices().forEach(target -> {
                // si el target sigue existiendo en la version 2, si no es vecino actual y si no es el mismo nodo que estamos evaluando
                if (graphV2.getVertices().contains(target) && !currentNeighbors.contains(target) && !node.equals(target)) {

                    // cada row es de la forma [Node, Target, Sim1, Sim2, ..., Realidad]
                    // crear las 2 primeras fileas Node y Target
                    String[] row = new String[2 + sims.size() + 1];
                    row[0] = node;
                    row[1] = target;

                    // setear los scores en "0.0" por defecto para evitar valores nulos en algunas tecnicas de lp
                    for (int i = 0; i < sims.size(); i++) {
                        row[i + 2] = "0.0";
                    }
                    boolean hasAtLeastOneScore = false;

                    for (int i = 0; i < sims.size(); i++) {
                        Similarity sim = sims.get(i);
                        float value = sim.score(graphV1, node, target);
                        log.info("nodo " + node + " target " + target + " score " + value + " lp " + sim.getName());
                        if (Float.isFinite(value) && value > 0) {
                            row[i + 2] = String.valueOf(value);
                            hasAtLeastOneScore = true;
                        }
                    }
                    //si todos los scores son 0 no lo agrego resultado final para evitar ruido
                    if (hasAtLeastOneScore) {
                        // verificamos si la dependencia existe en V2
                        Dependency depToCheck = new Dependency(node, target, 0);
                        row[row.length - 1] = dependenciesFromNextVersion.contains(depToCheck) ? "1" : "0";
                        body.add(row);
                    }
                }
            });
        });
        return body;
    }

}