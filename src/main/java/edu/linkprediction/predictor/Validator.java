package edu.linkprediction.predictor;

import com.filekeys.util.csv.CsvUtil;
import edu.linkprediction.parser.Dependency;
import edu.linkprediction.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class Validator {

    public static String[] getStats(List<Dependency> dependencias, List<Dependency> dependenciasAPredecir) {
        double tp = 0;
        double fp = 0;
        double fn = 0;
        for (Dependency d : dependencias) {
            if (dependenciasAPredecir.contains(d)) { // si la dependencia predicha existe realmente en la proxima version. Verdadero positivo
             //  log.info("Dependencia encontrada " + d);
                tp++;
            } else { // si la dependencia predicha en realidad no existe en la siguiente version. Falso positivo
                fp++;
            }
        }
        for (Dependency d : dependenciasAPredecir) {
            if (!dependencias.contains(d)) { // si una dependencia a predecir no fue predicha. Falso negativo.
               // log.info("dependencia faltante " + d.toString());
                fn++;
            }
        }

        log.info("TP " + tp);
        log.info("FP " + fp);
        log.info("FN " + fn);

        double recall = tp / (tp + fn);
        double precision = tp / (tp + fp);

        log.info("Recall " + recall);
        log.info("Precision " + precision);

        DecimalFormat df = new DecimalFormat("##.###");
        df.setRoundingMode(RoundingMode.DOWN);
        return new String[]{String.valueOf(df.format(recall)), String.valueOf(df.format(precision))};
    }

    public static void writeResults (HashMap<String, HashMap<String, List<Dependency>>> resultados, List<Dependency> dependenciasAPredecir, String path){
        List<String[]> body = new ArrayList<>();
        resultados.forEach((s, stringListHashMap) -> {
            log.info("resultados de: "+s);
            stringListHashMap.forEach((s1, dependencies) -> {
                log.info("resultados para el threshold: "+s1);
                body.add(Utils.getRow(getStats(dependencies, dependenciasAPredecir),s,s1));
            });
        });

        CsvUtil.write(
                body,
                CsvUtil.DEFAULT_HEADERS,
                path);
    }
}
