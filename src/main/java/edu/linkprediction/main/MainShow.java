package edu.linkprediction.main;

import edu.linkprediction.parser.*;
import edu.uci.ics.jung.graph.Graph;
import org.jdom2.JDOMException;

import java.io.IOException;

public class MainShow {

    public static void main(String[] args) throws Exception {
        showGraph();
    }

    private static void showGraph() throws IOException, JDOMException {
        XmlParser odem = new OdemParser();

        ParserJung parserJ = new ParserJung("src/main/resources/sistemas/subscriberDB-odem/sdb1.odem", odem);
        Graph<String, Integer> graphV1 = (Graph<String, Integer>) parserJ.getGraph();
        parserJ.showUP();

        Parser parserG = new ParserGephi("src/main/resources/sistemas/MobileMedia-odem/mobilemedia2.odem", odem);
        parserG.showGraph();
    }

}
