package edu.linkprediction.validation;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.utils.Utils;
import edu.uci.ics.jung.graph.Graph;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MicroAvgValidator {

    /**
     * MICRO AVERAGE
     * Calcula la precision y el recall por todo el grafo
     * @param nextVersion
     * @param prediction
     * @param original
     * @return
     */
    public String[] getStats(Graph<String, Integer> nextVersion, List<Dependency> prediction,
                                                  Graph<String, Integer> original) {

        DecimalFormat df = new DecimalFormat("##.###");
        df.setRoundingMode(RoundingMode.DOWN);

        List<Dependency> predecibles = Utils.getListaDependenciasPredecibles(original, nextVersion);
        AtomicInteger aciertos = new AtomicInteger();
        AtomicInteger fallos = new AtomicInteger();
        aciertos.set(0);
        fallos.set(0);

        // set es mas eficiente que list para realizar las busquedas
        Set<Dependency> predeciblesSet = new HashSet<>(Utils.getListaDependenciasPredecibles(original, nextVersion));

        prediction.forEach( dependencyPredicted -> {
            // me fijo si la dependencias predicha esta entre las predicibles entonces es un acierto
            if (predeciblesSet.contains(dependencyPredicted)) {
                aciertos.incrementAndGet();
            } else {
                fallos.incrementAndGet();
            }
        });

        double TP = aciertos.get();
        double FP = fallos.get();

        double TotalReales = predecibles.size();
        // FN = (Dependencias Reales) - (TP)
        double FN = TotalReales - TP;

        // Precision: TP / (TP + FP)
        double precision = TP / (TP + FP);

        // Recall: TP / (TP + FN)
        double recall = TP / (TP + FN);

        return new String[]{String.valueOf(df.format(recall)),String.valueOf(df.format(precision))};
    }
}
