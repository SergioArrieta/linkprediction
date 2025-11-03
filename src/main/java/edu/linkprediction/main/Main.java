package edu.linkprediction.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.filekeys.util.csv.CsvUtil;

import edu.linkprediction.parser.*;
import edu.linkprediction.parser.forpackage.ParserPackage;
import edu.linkprediction.predictor.Predictor;
import edu.linkprediction.predictor.PredictorByGraph;
import edu.linkprediction.predictor.PredictorByNode;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.ranking.RankingIndividual;
import edu.linkprediction.similarityMetrics.*;
import edu.linkprediction.utils.Utils;
import edu.uci.ics.jung.graph.Graph;
import org.jdom2.JDOMException;

public class Main {

    public static void main(String[] args) throws Exception {
       //showGraph();
       //predictByPackage();
       //predictByClasses();
        testing();
    }

    private static void predictByPackage() throws Exception {
        ParserPackage parserCurrent = new ParserPackage("src/main/resources/sistemas/MobileMedia-odem/mobilemedia2.odem");
        ParserPackage parserNext = new ParserPackage("src/main/resources/sistemas/MobileMedia-odem/mobilemedia3.odem");

        Graph<String, Integer> graphV1 = parserCurrent.getGraph();
        Graph<String, Integer> graphV2 = parserNext.getGraph();

        List<Similarity> similarities = new ArrayList<>();
        similarities.add(new AdamicAdar(Utils.getThresholdsList(3, 0.1, 1)));
        similarities.add(new CommonNeighbors(Utils.getThresholdsList(3, 0.1, 1)));
        similarities.add(new Sorensen(Utils.getThresholdsList(3, 0.1, 0.6)));
        similarities.add(new ResourceAllocation(Utils.getThresholdsList(3, 0.1, 0.3)));
        similarities.add(new HubPromoted(Utils.getThresholdsList(3, 0.1, 0.15)));
        similarities.add(new HubDepressed(Utils.getThresholdsList(3, 0.1, 0.09)));

        Ranking ranking = new RankingIndividual();

        Predictor predictor = new PredictorByGraph();

        CsvUtil.write(
                predictor.generateFullPrediction(graphV1,graphV2,similarities,ranking),
                CsvUtil.DEFAULT_HEADERS,
                "src/main/resources/sistemas/MobileMedia-odem/result.csv");
    }

    private static void predictByClasses() throws IOException, JDOMException {
        XmlParser odem = new OdemParser();
        Parser parserCurrent = new ParserJung("src/main/resources/sistemas/MobileMedia-odem/mobilemedia2.odem", odem);
        Parser parserNext = new ParserJung("src/main/resources/sistemas/MobileMedia-odem/mobilemedia3.odem", odem);
        Graph<String, Integer> graphV1 = (Graph<String, Integer>) parserCurrent.getGraph();
        Graph<String, Integer> graphV2 = (Graph<String, Integer>) parserNext.getGraph();

        List<Similarity> similarities = new ArrayList<>();
        similarities.add(new AdamicAdar(Utils.getThresholdsList(10, 0.1, 1)));
        similarities.add(new CommonNeighbors(Utils.getThresholdsList(10, 0.1, 1)));
        similarities.add(new Sorensen(Utils.getThresholdsList(3, 0.1, 0.6)));
        similarities.add(new ResourceAllocation(Utils.getThresholdsList(3, 0.1, 0.3)));
        similarities.add(new HubPromoted(Utils.getThresholdsList(3, 0.1, 0.15)));
        similarities.add(new HubDepressed(Utils.getThresholdsList(3, 0.1, 0.09)));
        similarities.add(new PreferentialAttachment(Utils.getThresholdsList(3, 0.1, 29)));
        similarities.add(new CoeficienteDeJaccard(Utils.getThresholdsList(3, 0.1, 0.4)));
        similarities.add(new Katz(Utils.getThresholdsList(3, 0.1, 1)));

        Ranking ranking = new RankingIndividual();

        Predictor predictor = new PredictorByGraph();
        CsvUtil.write(
                predictor.generateFullPrediction(graphV1,graphV2,similarities,ranking),
                CsvUtil.DEFAULT_HEADERS,
                "src/main/resources/sistemas/MobileMedia-odem/result.csv");
    }

    private static void showGraph () throws IOException, JDOMException {
        XmlParser odem = new OdemParser();

        ParserJung parserJ = new ParserJung("src/main/resources/sistemas/subscriberDB-odem/sdb1.odem", odem);
        Graph<String, Integer> graphV1 = (Graph<String, Integer>) parserJ.getGraph();
        parserJ.showUP();

        Parser parserG = new ParserGephi("src/main/resources/sistemas/MobileMedia-odem/mobilemedia2.odem", odem);
        parserG.showGraph();
    }

    private static void testing () throws IOException, JDOMException {

        XmlParser odem = new OdemParser();
        Parser parserCurrent = new ParserJung("src/main/resources/sistemas/MobileMedia-odem/mobilemedia2.odem", odem);
        Parser parserNext = new ParserJung("src/main/resources/sistemas/MobileMedia-odem/mobilemedia3.odem", odem);
        Graph<String, Integer> graphV1 = (Graph<String, Integer>) parserCurrent.getGraph();
        Graph<String, Integer> graphV2 = (Graph<String, Integer>) parserNext.getGraph();

        List<Similarity> similarities = new ArrayList<>();
        similarities.add(new AdamicAdar(Utils.getThresholdsList(10, 0.1, 1)));

        Ranking ranking = new RankingIndividual();

        Predictor predictor = new PredictorByNode();
        CsvUtil.write(
                predictor.generateFullPrediction(graphV1,graphV2,similarities,ranking),
                CsvUtil.DEFAULT_HEADERS,
                "src/main/resources/sistemas/MobileMedia-odem/result.csv");
    }
}
