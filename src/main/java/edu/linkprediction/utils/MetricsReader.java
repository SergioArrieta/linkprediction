package edu.linkprediction.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;

public class MetricsReader {

	// Almacena todas las metricas entre cada par de nodos
	List<String[]> Metrics;

	public MetricsReader(String path) throws IOException {

		CSVReader reader = new CSVReader(new FileReader(path), ';', '\''); //esto funciona aun?
		Metrics = reader.readAll();
		reader.close();
	}

	public String[] metrics(String n1, String n2) { // devuelve el arreglo que contiene las metricas entre 2 nodos
		for (String[] s : Metrics)
			if ((s[0].equals(n1)) && (s[1].equals(n2)))
				return s;

		return null;
	}

}
