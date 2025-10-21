package edu.linkprediction.ranking;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.linkprediction.parser.Dependency;

public class RankingIndividual extends Ranking {

    public RankingIndividual() {
        super.name = "Ranking Individual";
    }

	public List<Dependency> rank(List<Dependency> dependencies) {
		return dependencies.stream().sorted(Comparator.comparingDouble(Dependency::getScore).reversed()).collect(
				Collectors.toList());
	}
}
