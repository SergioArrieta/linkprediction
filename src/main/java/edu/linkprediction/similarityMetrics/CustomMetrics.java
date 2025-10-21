package edu.linkprediction.similarityMetrics;

import java.io.IOException;
import java.util.Objects;

import edu.linkprediction.utils.MetricsReader;
import edu.uci.ics.jung.graph.Graph;

public class CustomMetrics extends Similarity {

	MetricsReader reader; // para buscar las metricas entre 2 nodos
	
	public CustomMetrics(String path) throws IOException {
		reader = new MetricsReader(path);
	}
	
	public void setPath(String path)throws IOException {
		reader = new MetricsReader(path);
	}

	@Override
	public float score(Graph<String, Integer> g, String n1, String n2) {
		String[] s = reader.metrics(n1, n2);

		if (Objects.nonNull(s)) {
			// calcular norma del vector
			float suma = 0;
			for (int i = 2; i < s.length; i++) {
				suma = suma + Float.parseFloat(s[i]) * Float.parseFloat(s[i]);
				suma = (float) Math.sqrt(suma);
			}
			return suma;
		}
		return 0;
	}

}
