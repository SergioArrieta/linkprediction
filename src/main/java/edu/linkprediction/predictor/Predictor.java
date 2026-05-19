package edu.linkprediction.predictor;

import edu.linkprediction.parser.Dependency;
import edu.linkprediction.similarityMetrics.Similarity;
import edu.linkprediction.threshold.Threshold;
import edu.linkprediction.utils.Utils;
import edu.uci.ics.jung.graph.Graph;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ConcurrentHashMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Predictor {

    public List<Dependency> getResults(Similarity similarity, Graph<String, Integer> graphV1, Graph<String, Integer> graphV2, Threshold tr) {
        List<Dependency> results = new ArrayList<>();
        graphV1.getVertices().forEach(nodo -> {
            if (graphV2.containsVertex(nodo)) { // si el nodo no existe en la proxima version no tiene sentido calcular nada
                List<Dependency> dependenciasNodo = new ArrayList<>();

                // Recorro todos los nodos del grafo 1 para calcular el score de nodo con target
                graphV1.getVertices().forEach(target -> {
                    //verificar si target existe en la siguiente version, si nodo = target o si ya es vecino
                    if (!nodo.equals(target) && graphV2.containsVertex(target) && !graphV1.getNeighbors(nodo).contains(target)) {
                        dependenciasNodo.add(new Dependency(nodo, target, applySim(similarity, graphV1, nodo, target)));
                    }
                });
                // ordenamos la lista
                List<Dependency> dependenciasNodoOrdenadas =  dependenciasNodo.stream()
                        .sorted(Comparator.comparingDouble(Dependency::getScore).reversed())
                        .collect(Collectors.toList());
                results.addAll(tr.getListFromThreshold(dependenciasNodoOrdenadas));
            }
        });
        return results;
    }

    private float applySim(Similarity sim,Graph<String, Integer> graphV1, String nodo, String target ){
        float value = sim.score(graphV1, nodo, target);
        // algunas tecnicas de lp si no pueden calcular el valor porque no hay caminos disponibles tiran nan
        if (Float.isNaN(value) || value < 0) {
            value = 0;
        }
        return value;
    }

    public HashMap<String, HashMap<String, List<Dependency>>> getResults(List<Similarity> similarities, Graph<String, Integer> graphV1, Graph<String, Integer> graphV2) {

        HashMap<String, HashMap<String, List<Dependency>>> dependenciesBySimByTr= new HashMap<>();

        //recorrer la lista de tecnicas de lp
        similarities.forEach(similarity -> {

            //recorrer todo el grafo de la version 1
            graphV1.getVertices().forEach(nodo -> {

                if (graphV2.containsVertex(nodo)) { // si el nodo no existe en la proxima version no tiene sentido calcular nada
                    List<Dependency> dependenciasNodo = new ArrayList<>();
                    Set<String> vecinosNodo = new HashSet<>(graphV1.getNeighbors(nodo));
                    // Recorrer nuevamente todos los nodos del grafo 1 para calcular el score de "nodo" con "target"
                    graphV1.getVertices().forEach(target -> {
                        //verificar si target existe en la siguiente version,nodo = target o si ya es vecino
                        if (!nodo.equals(target) && graphV2.containsVertex(target) && !vecinosNodo.contains(target)) {
                            dependenciasNodo.add(new Dependency(nodo, target, applySim(similarity, graphV1, nodo, target)));
                        }
                    });

                    // ordenamos la lista
                    List<Dependency> dependenciasNodoOrdenadas =  dependenciasNodo.stream()
                            .sorted(Comparator.comparingDouble(Dependency::getScore).reversed())
                            .collect(Collectors.toList());

                    //aplicar thresholds
                    similarity.getThresholdList().forEach(threshold -> {
                        HashMap<String, List<Dependency>> dependenciesByTr = dependenciesBySimByTr.computeIfAbsent(similarity.getName(), k -> new HashMap<>());
                        dependenciesByTr.merge(
                                threshold.getName(),
                                threshold.getListFromThreshold(dependenciasNodoOrdenadas),
                                (dependenciesByTrOld, dependenciesByTrNew) -> {
                                    dependenciesByTrOld.addAll(dependenciesByTrNew);
                                    return dependenciesByTrOld;
                                }
                        );
                    });
                }
            });

        });
        return dependenciesBySimByTr;
    }

    //hay que guardar los resultados en un csv asi que es mejor hacerlo durante el recorrido
    //como los resultados son guardados en un csv para usarlos por el script en python no necesita los thresholds
    public void generateResultsForRoc(Graph<String, Integer> graphV1, Graph<String, Integer> graphV2, List<Similarity> sims, String outputPath) {
        List<Dependency> dependenciesV2 = Utils.getDependiciesFromGraph(graphV2);
        Collection<String> vertices = graphV1.getVertices();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputPath)))) {
            //escribo los headers: Node, Target, Adamic Adar, Katz...., Realidad
            String header = "Node;Target;" + sims.stream().map(Similarity::getName).collect(Collectors.joining(";")) + ";Realidad";
            writer.println(header);

            //proceso los nodos en paralelo
            vertices.parallelStream().forEach(node -> {
                Set<String> currentNeighbors = new HashSet<>(graphV1.getSuccessors(node));

                for (String target : vertices) {
                    //filtrar si el target no es el mismo nodo que estamos evaluando, si no es ya vecino o si no existe en la version 2
                    if (node.equals(target) || currentNeighbors.contains(target) || !graphV2.getVertices().contains(target)) {
                        continue;
                    }

                    StringBuilder row = new StringBuilder(node).append(";").append(target);
                    boolean hasAtLeastOneScore = false;
                    List<String> scores = new ArrayList<>();

                    for (Similarity sim : sims) {
                        float value = sim.score(graphV1, node, target);

                        if (Float.isFinite(value) && value > 0) {
                            scores.add(String.valueOf(value));
                            hasAtLeastOneScore = true;
                        } else {
                            //agrego 0 en casos de NaN
                            scores.add("0.0");
                        }
                    }

                    if (hasAtLeastOneScore) {
                        // pongo 1 o 0 dependende si la dependencia predicha existe o no realmente en la version 2
                        String reality = dependenciesV2.contains(new Dependency(node, target, 0)) ? "1" : "0";
                        String line = row.append(";").append(String.join(";", scores)).append(";").append(reality).toString();

                        synchronized (writer) {
                            writer.println(line);
                        }
                    }
                }
            });
        } catch (IOException e) {
            log.error("Error escribiendo el CSV de predicción", e);
        }
    }


    public HashMap<String, HashMap<String, List<Dependency>>> getResultsConcurrent(
            List<Similarity> similarities, Graph<String, Integer> graphV1, Graph<String, Integer> graphV2) {

        // Cambiamos a ConcurrentHashMap para que múltiples hilos escriban al mismo tiempo sin romper la estructura
        ConcurrentHashMap<String, ConcurrentHashMap<String, List<Dependency>>> dependenciesBySimByTr = new ConcurrentHashMap<>();

        // 1. Recorrer las técnicas de LP (Secuencial o Paralelo, se sugiere secuencial para no sobrecargar)
        similarities.forEach(similarity -> {

            // 2. TRUCO MAESTRO: Convertimos el recorrido de nodos a stream paralelo
            graphV1.getVertices().parallelStream().forEach(nodo -> {

                if (graphV2.containsVertex(nodo)) {
                    List<Dependency> dependenciasNodo = new ArrayList<>();

                    // El bucle interno se queda secuencial para que un hilo resuelva la vecindad completa de este nodo
                    graphV1.getVertices().forEach(target -> {
                        if (!nodo.equals(target) && graphV2.containsVertex(target) && !graphV1.getNeighbors(nodo).contains(target)) {
                            dependenciasNodo.add(new Dependency(nodo, target, applySim(similarity, graphV1, nodo, target)));
                        }
                    });

                    // Ordenamos localmente (operación rápida en memoria de este hilo)
                    List<Dependency> dependenciasNodoOrdenadas = dependenciasNodo.stream()
                            .sorted(Comparator.comparingDouble(Dependency::getScore).reversed())
                            .collect(Collectors.toList());

                    // 3. Aplicar thresholds con sincronización segura
                    similarity.getThresholdList().forEach(threshold -> {
                        ConcurrentHashMap<String, List<Dependency>> dependenciesByTr =
                                dependenciesBySimByTr.computeIfAbsent(similarity.getName(), k -> new ConcurrentHashMap<>());

                        List<Dependency> filtradas = threshold.getListFromThreshold(dependenciasNodoOrdenadas);

                        // Sincronizamos la escritura en la lista compartida para evitar condiciones de carrera (Race Conditions)
                        dependenciesByTr.merge(
                                threshold.getName(),
                                filtradas,
                                (oldList, newList) -> {
                                    synchronized (oldList) {
                                        oldList.addAll(newList);
                                    }
                                    return oldList;
                                }
                        );
                    });
                }
            });
        });

        // Convertimos de vuelta al tipo de retorno esperado por compatibilidad (HashMap tradicional)
        HashMap<String, HashMap<String, List<Dependency>>> resultadoFinal = new HashMap<>();
        dependenciesBySimByTr.forEach((simKey, trMap) -> {
            resultadoFinal.put(simKey, new HashMap<>(trMap));
        });

        return resultadoFinal;
    }
}