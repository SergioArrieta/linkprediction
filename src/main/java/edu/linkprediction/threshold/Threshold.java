package edu.linkprediction.threshold;

import java.util.List;

import lombok.Getter;

import edu.linkprediction.parser.Dependency;

@Getter
public abstract class Threshold {

    protected String name;
	public abstract List<Dependency> getListFromThreshold(List<Dependency> list);

}
