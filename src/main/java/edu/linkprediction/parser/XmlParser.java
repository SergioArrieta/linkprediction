package edu.linkprediction.parser;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.jdom2.JDOMException;

public abstract class XmlParser {

	Set<String> clases;
	Map<String, Map<String, Float>> outRelations;
	Map<String, Map<String, Float>> inRelations;
	Map<String, String> classNamespace;
	Map<String, Set<String>> namespaces;
	
	public abstract void parseArchive(String path) throws JDOMException, IOException;
	
	public abstract void set(Object...objects);

	public abstract String getSuffix();
}
