package edu.linkprediction.validation;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.utils.Utils;
import edu.uci.ics.jung.graph.Graph;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MacroAvgValidator {

    /**
     * MACRO AVERAGE
     * Calcula la precision y el recall por cada nodo y luego calcula un promedio. Más optimo que el Micro average
     * y el impacto de las dependencias predecidas de un mismo nodo tiene un impacto menor sobre el resultado final
     * REQUIERE HABER USADO EL PredictorByNode
     * @param nextVersion
     * @param prediction hashmap donde la key es el nodo y el elemento es la lista de dependencias que se predijeron para ese nodo
     * @param original grafo original que se uso para la prediccion
     * @return
     */
    public String[] getStats(Graph<String, Integer> nextVersion, HashMap<String, List<Dependency>> prediction,
                                         Graph<String, Integer> original) {

        DecimalFormat df = new DecimalFormat("##.###");
        df.setRoundingMode(RoundingMode.DOWN);

        List<Double> precisiones = new ArrayList<>();
        List<Double> recalles = new ArrayList<>();

        HashMap<String, List<Dependency>> predecibles = Utils.getDependenciasPredecibles(original, nextVersion);
        // recorro la lista de dependencias predecibles y sumo un acierto o un fallo dependiendo si mi lista "prediction"
        // incluye o no la dependencia que era predecible
        prediction.forEach((node, predichos) -> {

            AtomicInteger aciertos = new AtomicInteger();
            AtomicInteger fallos = new AtomicInteger();

            aciertos.set(0);
            fallos.set(0);
            if (Objects.nonNull(predecibles.get(node))) {
                predecibles.get(node).forEach(dependenciaPredecible -> {
                    if (predichos.contains(dependenciaPredecible)) { // me fijo si la dependencias predicha esta entre las predicibles entonces es un acierto
                        aciertos.incrementAndGet();
                    } else {
                        fallos.incrementAndGet();
                    }
                });
            }

            // prediction.get(node).size() es la cantidad de predicciones hechas finalmente, es decir la suma entre TP y FP
            double TPFP =  prediction.get(node).size();
            double TP = aciertos.get();

            //precision: TP / (TP + FP) ... si FP es cero entonces quiere decir que no se predijo ninguna dependencia erronea y entonces la precision es 1
            double precision = TP  / TPFP;
            precisiones.add(precision);

            double FN = fallos.get();
            //recall = TP / (TP + FN) .... FN es cero entonces quiere decir que no falto predecir ninguna dependencia y el recall 1
            double recall = TP / (TP + FN);
            recalles.add(recall);
        });

        double recall = recalles.stream().mapToDouble(a-> a).average().orElse(Double.NaN);
        double precision = precisiones.stream().mapToDouble(a-> a).average().orElse(Double.NaN);

        return new String[]{String.valueOf(df.format(recall)),String.valueOf(df.format(precision))};
    }
}
