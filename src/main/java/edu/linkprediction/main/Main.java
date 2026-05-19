package edu.linkprediction.main;

import edu.linkprediction.parser.*;
import edu.linkprediction.parser.forpackage.ParserPackage;
import edu.linkprediction.predictor.Predictor;
import edu.linkprediction.predictor.Validator;
import edu.linkprediction.similarityMetrics.*;
import edu.linkprediction.utils.Parameters;
import edu.linkprediction.utils.Utils;
import edu.uci.ics.jung.graph.Graph;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, JDOMException {
      //predictByPackage(Parameters.HIBERNATE, Parameters.HIBERNATE_NAMES);
      predictByClass(Parameters.HIBERNATE, Parameters.HIBERNATE_NAMES);
    }

    public static void predictByPackage(String[] versiones, String[] nombres) throws IOException, JDOMException {
        for (int i = 0; i < versiones.length - 1; i++) {
            ParserPackage parserCurrent = new ParserPackage(versiones[i]);
            Graph<String, Integer> graphV1 = parserCurrent.getGraph();

            ParserPackage parserNext = new ParserPackage(versiones[i + 1]);
            Graph<String, Integer> graphV2 = parserNext.getGraph();

            Predictor predictor = new Predictor();
            HashMap<String, HashMap<String, List<Dependency>>> resultados = predictor.getResults(
                    Parameters.getSimilaritiesBigSystems(graphV1),
                    graphV1,
                    graphV2);
            List<Dependency> dependenciasAPredecir = Utils.getRealDependencies(graphV1, graphV2);
            Validator.writeResults(resultados, dependenciasAPredecir, "target/package_result_" + nombres[i] + ".csv");
        }
    }

    public static void predictByClass(String[] versiones, String[] nombres) throws IOException, JDOMException {
        XmlParser odem = new OdemParser();
        for (int i = 0; i < versiones.length - 1; i++) {
            Parser parserCurrent = new ParserJung(versiones[i], odem);
            Graph<String, Integer> graphV1 = (Graph<String, Integer>) parserCurrent.getGraph();

            Parser parserNext = new ParserJung(versiones[i + 1], odem);
            Graph<String, Integer> graphV2 = (Graph<String, Integer>) parserNext.getGraph();

            Predictor predictor = new Predictor();
            HashMap<String, HashMap<String, List<Dependency>>> resultados = predictor.getResults(
                    Parameters.getSimilaritiesBigSystemsClass(graphV1),
                    graphV1,
                    graphV2);
            List<Dependency> dependenciasAPredecir = Utils.getRealDependencies(graphV1, graphV2);
            Validator.writeResults(resultados, dependenciasAPredecir, "target/class_result_" + nombres[i] + ".csv");
        }
    }

}

