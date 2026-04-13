package edu.linkprediction.ranking;

import edu.linkprediction.parser.Dependency;
import java.util.*;
import java.util.stream.Collectors;

public class RankFagin extends RankAggregation {

    private final int k;

    public RankFagin(int k) {
        super.name = "Fagin";
        this.k = k;
    }

    @Override
    public List<Dependency> rank(List<Dependency> d1, List<Dependency> d2) {
        Map<Dependency, Double> candidates = new HashMap<>();
        Set<Dependency> seenInD1 = new HashSet<>();
        Set<Dependency> seenInD2 = new HashSet<>();

        int n = Math.min(d1.size(), d2.size());
        int i = 0;

        while (i < n && candidates.size() < k) {
            Dependency dep1 = d1.get(i);
            Dependency dep2 = d2.get(i);

            processElement(dep1, d1, d2, candidates, seenInD1, seenInD2);
            if (candidates.size() < k) {
                processElement(dep2, d1, d2, candidates, seenInD2, seenInD1);
            }
            i++;
        }

        return candidates.entrySet().stream()
                .map(e -> new Dependency(e.getKey().getNodoA(), e.getKey().getNodoB(), e.getValue()))
                .sorted(Comparator.comparingDouble(Dependency::getScore).reversed())
                .limit(k)
                .collect(Collectors.toList());
    }

    private void processElement(Dependency dep, List<Dependency> list1, List<Dependency> list2,
                                Map<Dependency, Double> candidates, Set<Dependency> selfSeen, Set<Dependency> otherSeen) {
        selfSeen.add(dep);
        if (otherSeen.contains(dep)) {
            double score1 = getScoreFromList(dep, list1);
            double score2 = getScoreFromList(dep, list2);
            candidates.put(dep, score1 + score2);
        }
    }

    private double getScoreFromList(Dependency target, List<Dependency> list) {
        return list.stream()
                .filter(d -> d.equals(target))
                .findFirst()
                .map(Dependency::getScore)
                .orElse(0.0);
    }
}