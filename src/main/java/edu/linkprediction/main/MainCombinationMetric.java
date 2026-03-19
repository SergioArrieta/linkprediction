package edu.linkprediction.main;

import com.filekeys.util.csv.CsvUtil;
import edu.linkprediction.parser.forpackage.ParserPackage;
import edu.linkprediction.predictor.Predictor;
import edu.linkprediction.predictor.PredictorByGraph;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.ranking.RankingIndividual;
import edu.linkprediction.similarityMetrics.*;
import edu.linkprediction.utils.Utils;
import edu.uci.ics.jung.graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class MainCombinationMetric {

    public static void main(String[] args) throws Exception {
        predictByPackage();
    }

    private static void predictByPackage() throws Exception {
        ParserPackage parserCurrent = new ParserPackage("src/main/resources/sistemas/subscriberDB-odem/sdb1.odem");
        Graph<String, Integer> graphV1 = parserCurrent.getGraph();

        ParserPackage parserNext = new ParserPackage("src/main/resources/sistemas/subscriberDB-odem/sdb2.odem");
        Graph<String, Integer> graphV2 = parserNext.getGraph();

        List<Similarity> similarities = new ArrayList<>();
        similarities.add( new Combination(
                Utils.getThresholdsList(3, 0.1, 1),
                new AdamicAdar(Utils.getThresholdsList(3, 0.1, 1)),
                new CoeficienteDeJaccard(Utils.getThresholdsList(3, 0.1, 0.6)),
                0.5,
                0.5));

        Ranking ranking = new RankingIndividual();

        Predictor predictor = new PredictorByGraph();
        //Predictor predictor = new PredictorByNode();

        CsvUtil.write(
                predictor.generateFullPrediction(graphV1,graphV2,similarities,ranking),
                CsvUtil.DEFAULT_HEADERS,
                "src/main/resources/sistemas/subscriberDB-odem/result.csv");
    }
}
