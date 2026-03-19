package edu.linkprediction.ranking;

import edu.linkprediction.parser.Dependency;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class RankAggregation {

    protected String name;

    public abstract List<Dependency> rank(List<Dependency> dependencies1, List<Dependency> dependencies2);

}
