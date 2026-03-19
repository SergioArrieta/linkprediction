package edu.linkprediction.main;

import com.filekeys.util.csv.CsvUtil;
import edu.linkprediction.parser.*;
import edu.linkprediction.parser.forpackage.ParserPackage;
import edu.linkprediction.predictor.PredictorRankAggregation;
import edu.linkprediction.ranking.RankAggregation;
import edu.linkprediction.ranking.RankBorda;
import edu.linkprediction.similarityMetrics.*;
import edu.linkprediction.threshold.FirstElements;
import edu.linkprediction.threshold.Threshold;
import edu.linkprediction.utils.Utils;
import edu.uci.ics.jung.graph.Graph;

public class MainRankAggregation {

    public static void main(String[] args) throws Exception {
        ParserPackage parserCurrent = new ParserPackage("src/main/resources/sistemas/MobileMedia-odem/mobilemedia5.odem");
        Graph<String, Integer> graphV1 = parserCurrent.getGraph();

        ParserPackage parserNext = new ParserPackage("src/main/resources/sistemas/MobileMedia-odem/mobilemedia6.odem");;
        Graph<String, Integer> graphV2 = parserNext.getGraph();

        PredictorRankAggregation predictor = new PredictorRankAggregation(); //Actualmente toma la prediccion en base al grafo completo y no al nodo

        Similarity sim1 = new AdamicAdar(Utils.getThresholdsList(3, 0.1, 1));
        Similarity sim2 = new CoeficienteDeJaccard(Utils.getThresholdsList(3, 0.1, 1));

        RankAggregation rank = new RankBorda();
        Threshold threshold = new FirstElements(3);

        CsvUtil.write(
                predictor.generateFullPrediction(graphV1,graphV2,sim1,sim2,rank,threshold),
                CsvUtil.DEFAULT_HEADERS,
                "src/main/resources/sistemas/MobileMedia-odem/result.csv");
    }
}
