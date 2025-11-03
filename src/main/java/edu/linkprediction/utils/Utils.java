package edu.linkprediction.utils;

import java.util.*;
import java.util.stream.Collectors;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.similarityMetrics.Similarity;
import edu.linkprediction.threshold.CutPoint;
import edu.linkprediction.threshold.FirstElements;
import edu.linkprediction.threshold.Threshold;
import edu.linkprediction.threshold.Umbral;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Utils {
	
	/**
	 * convierte un grafo en una lista de dependencias
	 * @param graph
	 * @return
	 */
	public static List<Dependency> getDependiciesFromGraph(Graph<String, Integer> graph) {
		List<Dependency> dependencias = new ArrayList<>();
		for (int e : graph.getEdges()) {
			String nodo1 = graph.getEndpoints(e).getFirst();
			String nodo2 = graph.getEndpoints(e).getSecond();
			Dependency dep = new Dependency(nodo1, nodo2, 0);
			if (!dependencias.contains(dep)) {
				dependencias.add(dep);
			}
		}
		return dependencias;
	}

	/**
	 * conviertierte un grafo en una lista de dependencias y calcula un score para cada una
	 * @param graph
	 * @param similarity
	 * @param graphOriginal
	 * @return
	 */
	public static List<Dependency> getDependiciesFromGraphScore(Graph<String, Integer> graph, Similarity similarity,
			Graph<String, Integer> graphOriginal) {
		
		List<Dependency> dependencias = new ArrayList<>();
		for (int e : graph.getEdges()) {
			String nodo1 = graph.getEndpoints(e).getFirst();
			String nodo2 = graph.getEndpoints(e).getSecond();
			Dependency dep = new Dependency(nodo1, nodo2, similarity.score(graph, nodo1, nodo2));
			if (!dependencias.contains(dep)) {
				dependencias.add(dep);
			}
		}
		
		return dependencias;
	}
	
	/**
	 * convierte una lista de dependencias en un grafo
	 * @param dependencias
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Graph<String, Integer> getGraphFromDependecy(List<Dependency> dependencias) {
		Graph graph = new DirectedSparseGraph<String, Integer>();

		dependencias.forEach(dep -> {
			if (!graph.containsVertex(dep.getNodoA())) {
				graph.addVertex(dep.getNodoA());
			}
			if (!graph.containsVertex(dep.getNodoB())) {
				graph.addVertex(dep.getNodoB());
			}
			graph.addEdge(graph.getEdgeCount() + 1, dep.getNodoA(), dep.getNodoB());
		});

		return graph;
	}
	
	/**
	 * fusiona dos grafos
	 * @param graph1
	 * @param graph2
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Graph<String, Integer> mergeGraphs(Graph<String, Integer> graph1, Graph<String, Integer> graph2) {
		Graph graph = new DirectedSparseGraph<String, Integer>();

		for (String v : graph1.getVertices())
			if (!graph.containsVertex(v))
				graph.addVertex(v);
		for (String v : graph2.getVertices())
			if (!graph.containsVertex(v))
				graph.addVertex(v);

		for (Integer e : graph1.getEdges()) {
			graph.addEdge(graph.getEdgeCount() + 1, graph1.getEndpoints(e));
		}
		for (Integer e : graph2.getEdges()) {
			graph.addEdge(graph.getEdgeCount() + 1, graph2.getEndpoints(e));
		}

		return graph;
	}
	
	/**
	 * conviernte una lista de dependencias de un nodo en un grafo
	 * @param neighbours
	 * @param node
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Graph<String, Integer> fromListNeighboursToGraph(List<Dependency> neighbours, String node) {
		Graph<String, Integer> graphR = new DirectedSparseGraph<>();
		graphR.addVertex(node);

		neighbours.forEach(n -> {
			graphR.addVertex(n.getNodoB());
			graphR.addEdge(graphR.getEdgeCount() + 1, node, n.getNodoB());
		});

		return graphR;
	}
	
	/**
	 * elimina los nodos y sus aristas que existen en la siguiente version pero no en la actual.
     * Es decir elimina el nodo que aparecen en versiones posteriores para no ensuciar la prediccion.
	 *
	 *  Se cumple de la siguiente forma:
	  	Parser parser1 = new ParserJung("src/main/resources/sistemas/MobileMedia-odem/mobilemedia1.odem",odem);
		Parser parser2 = new ParserJung("src/main/resources/sistemas/MobileMedia-odem/mobilemedia2.odem",odem);
		
		Graph<String, Integer> graphV1 = (Graph<String, Integer>) parser1.getGraph();
		Graph<String, Integer> graphV2 = (Graph<String, Integer>) parser2.getGraph();

		Utils.getDependiciesFromGraph(graphV1).size() = Utils.getDependiciesFromGraph(Utils.removeNewVertex(graphV1, graphV2)).size()
	 * @param actualVersion
	 * @param nextVersion
	 * @return
	 */
	public static Graph<String, Integer> removeNewVertex(Graph<String, Integer> actualVersion, Graph<String, Integer> nextVersion) {
		Graph<String, Integer> graphR = new DirectedSparseGraph<>();

		//agrega todos los vertices originales
		actualVersion.getVertices().forEach(vertex -> graphR.addVertex(vertex));
		
		//recorre todas las aristas de la siguiente version y agrega solo las que estan relacionadas con los vertices actuales.
		nextVersion.getEdges().forEach(edge -> {
			if ((actualVersion.containsVertex(nextVersion.getEndpoints(edge).getFirst()))
					&& (actualVersion.containsVertex(nextVersion.getEndpoints(edge).getSecond()))) {
				graphR.addEdge(edge, nextVersion.getEndpoints(edge));
			}
		});
		return graphR;
	}

    /**
     * Devuelve un Hashmap con todas las dependencias predecibles por nodo. La key es el nodo.
     * Se calcula removiendo de la lista de dependencias de la proxima version, las dependencias ya existentes.
     * Una dependencia es no predicible si uno de sus 2 nodos no existe en la proxima version.
     * USADO EN MACROAVG
     **/
	public static HashMap<String, List<Dependency>> getDependenciasPredecibles(Graph<String, Integer> actualVersion, Graph<String, Integer> nextVersion) {

        final List<Dependency> originales = Utils.getDependiciesFromGraph(actualVersion);
        final Collection<String> nodosActuales = actualVersion.getVertices();

        List<Dependency> nextTodas = Utils.getDependiciesFromGraph(nextVersion);
        List<Dependency> listPredecibles = nextTodas.stream()
                .filter(d -> nodosActuales.contains(d.getNodoA()) && nodosActuales.contains(d.getNodoB()))
                .filter(d -> !originales.contains(d))
                .collect(Collectors.toList());

        HashMap<String, List<Dependency>> hashPredecibles = new HashMap<>();
        listPredecibles.forEach(dependencia -> {
            hashPredecibles.computeIfAbsent(dependencia.getNodoA(), k -> new ArrayList<>()).add(dependencia);
        });
        return hashPredecibles;
	}

    /**
     * USADO EN MICROAVG
     * @param actualVersion
     * @param nextVersion
     * @return
     */
    public static List<Dependency> getListaDependenciasPredecibles(Graph<String, Integer> actualVersion, Graph<String, Integer> nextVersion) {

        List<Dependency> originals = Utils.getDependiciesFromGraph(actualVersion);
        List<Dependency> next = Utils.getDependiciesFromGraph(Utils.removeNewVertex(actualVersion, nextVersion));

        return next.stream().filter(d -> !originals.contains(d)).collect(Collectors.toList());
    }

    public static String[] getRow(String[] stats, String similarityName, String thresholdName) {
        return new String[]{similarityName + " - " + thresholdName,
                stats[0].replace(".", ","),
                stats[1].replace(".", ",")};
    }

    public static List<Threshold> getThresholdsList(int k, double p, double u) {
        List<Threshold> thresholdList = new ArrayList<>();
        thresholdList.add(new FirstElements(k));
        thresholdList.add(new CutPoint(p));
        thresholdList.add(new Umbral(u));
        return thresholdList;
    }
}
