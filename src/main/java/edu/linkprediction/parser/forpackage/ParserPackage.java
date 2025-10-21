package edu.linkprediction.parser.forpackage;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jdom2.JDOMException;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class ParserPackage extends ParserAux {

	/*
	 * Similar al otro, solo que ahora arma el graph como paquetes! Ademas tiene que
	 * actualizar las estructuras!
	 */

	public ParserPackage(String path) throws JDOMException, IOException {
		super(path);
		updateStructures();
	}

	@Override
	protected Graph<String, Integer> createGraph() {
		System.out.println("Starts building graph... " + new Date());
		int numberEdge = 0;
		Graph<String, Integer> g = new DirectedSparseGraph<String, Integer>();

		for (String c : namespaces.keySet()) { // por cada namespace, agrego el arco
			g.addVertex(c);
		}

		for (String c : clases) {

			Map<String, Float> rel = outRelations.get(c);
			String nameSpaceC = classNamespace.get(c);
			if (rel != null) {

				for (String o : rel.keySet()) {
					String nameSpaceO = classNamespace.get(o);

					if (nameSpaceC.equals(nameSpaceO))
						continue;

					g.addEdge(numberEdge, nameSpaceC, nameSpaceO);
					numberEdge++;
				}
			}

			rel = inRelations.get(c);
			if (rel != null) {

				for (String o : rel.keySet()) {

					if (classNamespace.get(o).equals(nameSpaceC))
						continue;

					g.addEdge(numberEdge, classNamespace.get(o), nameSpaceC);
					numberEdge++;
				}
			}
		}
		System.out.println("Finish building graph... " + new Date());
		return g;
	}

	/*
	 * Las estructuras que estaban armadas de antes, ahora hay que borrarlas y
	 * reemplazarlas!
	 */
	private void updateStructures() {

		Map<String, Map<String, Float>> outAux = new HashMap<String, Map<String, Float>>();
		Map<String, Map<String, Float>> inAux = new HashMap<String, Map<String, Float>>();

		for (String n : namespaces.keySet()) {
			outAux.put(n, new HashMap<String, Float>());
			inAux.put(n, new HashMap<String, Float>());
		}

		for (String n : namespaces.keySet()) {
			Set<String> clasesIn = namespaces.get(n);
			for (String c : clasesIn) { // por todas las clases acá!

				Map<String, Float> cOut = outRelations.get(c);
				Map<String, Float> nOut = outAux.get(n);
				for (String co : cOut.keySet()) {
					String nameSpaceCO = classNamespace.get(co);

					if (n.equals(nameSpaceCO))
						continue;

					Float f = nOut.get(nameSpaceCO);
					if (f == null)
						f = 0f;
					f += cOut.get(co);
					nOut.put(nameSpaceCO, f);
				}

				// Ahora los in!
				Map<String, Float> cIn = inRelations.get(c);
				Map<String, Float> nIn = inAux.get(n);

				if (cIn == null)
					continue;

				for (String ci : cIn.keySet()) {
					String nameSpaceCI = classNamespace.get(ci);

					if (n.equals(nameSpaceCI))
						continue;

					Float f = nIn.get(nameSpaceCI);
					if (f == null)
						f = 0f;
					f += cIn.get(ci);
					nIn.put(nameSpaceCI, f);
				}
			}
		}

		outRelations = outAux;
		inRelations = inAux;
		clases.clear();
		clases.addAll(namespaces.keySet());

	}

	public String toString() {
		return "package";
	}
}
