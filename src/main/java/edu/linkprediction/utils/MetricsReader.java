package edu.linkprediction.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsReader {

	// Almacena todas las metricas entre cada par de nodos
	List<String[]> metrics;

	public MetricsReader(String path) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(path), ';', '\''); //esto funciona aun?
        metrics = reader.readAll();
		reader.close();
	}

	public String[] metrics(String n1, String n2) { // devuelve el arreglo que contiene las metricas entre 2 nodos

        log.info("similaries "+n1 + " "+n2);
        for (String[] s : metrics){
            log.info("similarities "+s[0]+" "+s[1]);
            if ((s[0].equals(n1)) && (s[1].equals(n2)))
                return s;
        }


		return null;
	}

    public List<String[]> getMetrics() {
        return metrics;
    }
}
