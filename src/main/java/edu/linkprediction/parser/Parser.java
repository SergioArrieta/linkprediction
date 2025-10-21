package edu.linkprediction.parser;

public abstract class Parser {

	protected Object graphContainer;

	public abstract void createdGraph();
	
	public abstract void showGraph();

	public Object getGraph() {
		return graphContainer;
	}

}
