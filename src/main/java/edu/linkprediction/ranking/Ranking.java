package edu.linkprediction.ranking;

import java.util.List;

import edu.linkprediction.parser.Dependency;

public abstract class Ranking {

	protected String name = "";

	public abstract List<Dependency> rank(List<Dependency> dependencies);
	
}