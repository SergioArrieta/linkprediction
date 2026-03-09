package edu.linkprediction.threshold;

import java.util.List;
import java.util.stream.Collectors;

import edu.linkprediction.parser.Dependency;

public class Umbral extends Threshold{
		
	private final double scoreMin;

    public Umbral(Double scoreMin) {
        this.scoreMin = scoreMin;
        super.name = "Umbral de " + scoreMin;
    }

    @Override
    public List<Dependency> getListFromThreshold(List<Dependency> list) {
        return list.stream().filter(n -> n.getScore() >= scoreMin).collect(Collectors.toList());
    }

}
