package edu.linkprediction.parser;

public abstract class Parser {

	protected Object graphContainer;

	public abstract void createGraph();
	
	public abstract void showGraph();

	public Object getGraph() {
		return graphContainer;
	}

}
