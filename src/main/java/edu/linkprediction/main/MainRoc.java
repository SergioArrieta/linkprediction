package edu.linkprediction.main;

import edu.linkprediction.parser.OdemParser;
import edu.linkprediction.parser.Parser;
import edu.linkprediction.parser.ParserJung;
import edu.linkprediction.parser.XmlParser;
import edu.linkprediction.predictor.Predictor;
import edu.linkprediction.similarityMetrics.*;
import edu.uci.ics.jung.graph.Graph;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MainRoc {

    public static void main(String[] args) throws Exception {
        //predictByClasses();
        predictByClassesSimilaritiesFromCSV();
    }
    private static void predictByClasses() throws IOException, JDOMException {
        XmlParser odem = new OdemParser();
        Parser parserCurrent = new ParserJung("src/main/resources/sistemas/subscriberDB-odem/sdb1.odem", odem);
        Graph<String, Integer> graphV1 = (Graph<String, Integer>) parserCurrent.getGraph();

        Parser parserNext = new ParserJung("src/main/resources/sistemas/subscriberDB-odem/sdb2.odem", odem);
        Graph<String, Integer> graphV2 = (Graph<String, Integer>) parserNext.getGraph();

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

        Predictor predictor = new Predictor();
        predictor.generateResultsForRoc(graphV1,graphV2,similarities,"target/subscriberDB_1_2_main_roc_result.csv");
    }
    private static void predictByClassesSimilaritiesFromCSV() throws IOException, JDOMException {
        XmlParser odem = new OdemParser();
        Parser parserCurrent = new ParserJung("src/main/resources/sistemas/subscriberDB-odem/sdb1.odem", odem);
        Graph<String, Integer> graphV1 = (Graph<String, Integer>) parserCurrent.getGraph();

        Parser parserNext = new ParserJung("src/main/resources/sistemas/subscriberDB-odem/sdb2.odem", odem);
        Graph<String, Integer> graphV2 = (Graph<String, Integer>) parserNext.getGraph();


        List<Similarity> similarities = new ArrayList<>();
        similarities.add(new SimilaritiesFromCSV(null,"src/main/resources/similarities/subscriberdb-similarities/RevistaKolbe_v1.0.zip-similarities-lexical-null-class.csv"));

        Predictor predictor = new Predictor();
        predictor.generateResultsForRoc(graphV1,graphV2,similarities,"target/subscriberDB_1_2_main_roc_result_similarities_fromCSV.csv");
    }

}
