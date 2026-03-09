package edu.linkprediction.similarityMetrics;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import edu.linkprediction.threshold.Threshold;
import edu.linkprediction.utils.MetricsReader;
import edu.uci.ics.jung.graph.Graph;

public class SimilaritiesFromCSV extends Similarity {

    private final MetricsReader reader;

    public SimilaritiesFromCSV(List<Threshold> thresholdList, String path) throws IOException {
        super.name = "SimilaritiesFromCSV: ";
        super.thresholdList = thresholdList;
        reader = new MetricsReader(path);
    }

    @Override
    public float score(Graph<String, Integer> g, String n1, String n2) {
        String[] s = reader.metrics(n1, n2);
    //Calcular la norma del vector
        if (Objects.nonNull(s) && s.length > 2) {
            float sumaCuadrados = 0;

            for (int i = 2; i < s.length; i++) {
                try {
                    float valor = Float.parseFloat(s[i]);
                    sumaCuadrados += valor * valor;
                } catch (NumberFormatException e) {
                    // parser error
                }
            }
            return (float) Math.sqrt(sumaCuadrados);
        }
        return 0;
    }

}