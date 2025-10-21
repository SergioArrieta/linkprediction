package edu.linkprediction.parser.forpackage;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class ParserAux {

	Set<String> clases; // se puede reemplazar por outRelations.keySet();
	Map<String, Map<String, Float>> outRelations;
	Map<String, Map<String, Float>> inRelations;
	Map<String, String> classNamespace;
	Map<String, Set<String>> namespaces;
	Map<String, String> containerAttributes;

	Graph<String, Integer> graph;

	public ParserAux(String path) throws JDOMException, IOException {
		clases = new HashSet<String>();
		outRelations = new HashMap<String, Map<String, Float>>();
		inRelations = new HashMap<String, Map<String, Float>>();
		classNamespace = new HashMap<String, String>();
		namespaces = new HashMap<String, Set<String>>();

		containerAttributes = new HashMap<String, String>();

		parseArchive(path);
		// System.out.println(namespaces);
		graph = createGraph();

	}

	/*
	 * Load file y filtra las clases basicas de java. Que filtrar recien se sabe
	 * luego de levantar todo el xml... asi que se hace al final En out no hace
	 * falta filtrar los keys
	 */
	private void parseArchive(String xmlSource) throws JDOMException, IOException {
		System.out.println("Starts parsing... " + new Date());
		SAXBuilder jdomBuilder = new SAXBuilder();
		jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Document jdomDocument = jdomBuilder.build(xmlSource);

		Element container = jdomDocument.getRootElement().getChild("context").getChild("container");

		List<Attribute> attsContainer = container.getAttributes();
		for (Attribute a : attsContainer) {
			containerAttributes.put(a.getName(), a.getValue());
		}

		List<Element> rootNode = container.getChildren("namespace");
		for (Element n : rootNode) { // namespaces
			List<Element> list = n.getChildren("type");
			String namespace = n.getAttributeValue("name");
			for (Element e : list) { // classes

				String c = e.getAttributeValue("name");
				classNamespace.put(c, namespace);
				// System.out.println(c);

				Set<String> ns = namespaces.get(namespace);
				if (ns == null) {
					ns = new HashSet<String>();
					ns.add(c);
					namespaces.put(namespace, ns);
				} else
					ns.add(c);

				clases.add(c);
				List<Element> dependencies = e.getChild("dependencies").getChildren();
				Map<String, Float> dep = new HashMap<String, Float>(); // out
				for (Element d : dependencies) { // dependencies
					String cd = d.getAttributeValue("name");
					dep.put(cd, 1.0f);
					Map<String, Float> in = inRelations.get(cd);
					if (in == null) { // in
						in = new HashMap<String, Float>();
						in.put(c, 1.0f);
						inRelations.put(cd, in);
					} else
						in.put(c, 1.0f);
				}
				outRelations.put(c, dep);
			}
		}

		// Filtering...
		inRelations.keySet().retainAll(clases);
		for (String c : clases) {
			outRelations.get(c).keySet().retainAll(clases);
			Map<String, Float> i = inRelations.get(c);
			if (i != null)
				i.keySet().retainAll(clases);
		}

	}

	/*
	 * TODO: parseArchive + buildGraph Weighted Graph
	 */
	protected Graph<String, Integer> createGraph() {
		System.out.println("Starts building graph... " + new Date());
		int numberEdge = 0;
		Graph<String, Integer> g = new DirectedSparseGraph<String, Integer>();

		for (String c : clases)
			g.addVertex(c);

		for (String c : clases) {
			// System.out.println(c);
			Map<String, Float> rel = outRelations.get(c);
			if (rel != null) {
				// System.out.println(rel);
				for (String o : rel.keySet()) {
					g.addEdge(numberEdge, c, o);
					numberEdge++;
				}
			}

		}
		// System.out.println(g+"\n");
		return g;
	}

	public Set<String> getNameSpaces() {
		return namespaces.keySet();
	}

	public String getNameSpace(String clas) {
		return classNamespace.get(clas);
	}

	public Map<String, Set<String>> getPackageDistribution() {
		return namespaces;
	}

	public Map<String, Float> getInRelations(String clas) {
		Map<String, Float> r = inRelations.get(clas);
		if (r == null)
			r = new HashMap<String, Float>();
		return r;
	}

	public Map<String, Float> getOutRelations(String clas) {
		Map<String, Float> r = outRelations.get(clas);
		if (r == null)
			r = new HashMap<String, Float>();
		return r;
	}

	public Set<String> getClasses() {
		return clases;
	}

	public Graph<String, Integer> getGraph() {
		return graph;
	}

	public String toString() {
		return "class";
	}

	// true si class1 depende de class2
	public Boolean dependsOn(String class1, String class2) {
		Map<String, Float> outClass1 = outRelations.get(class1);
		if (outClass1 == null)
			return false;
		return outClass1.containsKey(class2);
	}

}
