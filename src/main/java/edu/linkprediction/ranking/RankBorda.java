package edu.linkprediction.ranking;

import edu.linkprediction.parser.Dependency;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class RankBorda extends RankAggregation {

    public RankBorda() {
        super.name = "RankBorda";
    }

    @Override
    public List<Dependency> rank(List<Dependency> d1, List<Dependency> d2) {
        // combinar las listas en un solo dtream de entradas de mapa (dependency -> score)
        return Stream.concat(getBordaStream(d1), getBordaStream(d2))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,Double::sum)) // agrupar por dependency y sumamos los scores
                .entrySet().stream()
                // crear los objetos finales con el score acumulado
                .map(entry -> new Dependency(entry.getKey().getNodoA(), entry.getKey().getNodoB(), entry.getValue()))
                .sorted(Comparator.comparingDouble(Dependency::getScore).reversed()) // ordenar de mayor a menor score
                .collect(Collectors.toList());
    }

    private Stream<Map.Entry<Dependency, Double>> getBordaStream(List<Dependency> list) {
        int size = list.size();
        return IntStream.range(0, size).mapToObj(i -> Map.entry(list.get(i), (double) (size - i)));
    }
}