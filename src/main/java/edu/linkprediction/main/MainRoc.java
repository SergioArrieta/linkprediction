package edu.linkprediction.main;

import com.filekeys.util.csv.CsvUtil;
import edu.linkprediction.parser.OdemParser;
import edu.linkprediction.parser.Parser;
import edu.linkprediction.parser.ParserJung;
import edu.linkprediction.parser.XmlParser;
import edu.linkprediction.predictor.PredictorRoc;
import edu.linkprediction.ranking.Ranking;
import edu.linkprediction.ranking.RankingIndividual;
import edu.linkprediction.similarityMetrics.*;
import edu.linkprediction.threshold.CutPoint;
import edu.uci.ics.jung.graph.Graph;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainRoc {

    public static void main(String[] args) throws Exception {
        predictByClasses();
    }
    private static void predictByClasses() throws IOException, JDOMException {
        XmlParser odem = new OdemParser();
        Parser parserCurrent = new ParserJung("src/main/resources/sistemas/MobileMedia-odem/mobilemedia5.odem", odem);
        Graph<String, Integer> graphV1 = (Graph<String, Integer>) parserCurrent.getGraph();

        Parser parserNext = new ParserJung("src/main/resources/sistemas/MobileMedia-odem/mobilemedia7.odem", odem);
        Graph<String, Integer> graphV2 = (Graph<String, Integer>) parserNext.getGraph();

        Ranking ranking = new RankingIndividual();
        PredictorRoc predictor = new PredictorRoc();
        
        List<Similarity> similarities = new ArrayList<>();
        similarities.add(new AdamicAdar(null));
        similarities.add(new CoeficienteDeJaccard(null));
        similarities.add(new CommonNeighbors(null));
        similarities.add(new HubPromoted(null));
        similarities.add(new HubDepressed(null));
        similarities.add(new PreferentialAttachment(null));
        similarities.add(new ResourceAllocation(null));
        similarities.add(new Sorensen(null));
        similarities.add(new Katz(null));
        similarities.add(new SimRank(graphV1, null));

        List<String> simNames = similarities.stream().map(Similarity::getName).collect(Collectors.toList());

        List<String> header = new ArrayList<>();
        header.add("Node");
        header.add("Target");
        header.addAll(simNames);
        header.add("Realidad");

        CsvUtil.write(
                predictor.generateFullPrediction(
                        graphV1,
                        graphV2,
                        similarities,
                        ranking,
                        new CutPoint(0.4)),
                header,
                "target/main_roc_result.csv");
    }
}
