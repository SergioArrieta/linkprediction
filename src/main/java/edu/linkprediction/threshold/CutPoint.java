package edu.linkprediction.threshold;

import java.util.ArrayList;
import java.util.List;

import edu.linkprediction.parser.Dependency;

public class CutPoint extends Threshold {

	private final double percent;

    public CutPoint(double p) {
        this.percent = p;
        name = "CutPoint " + p;
    }

	/**
	 * RECORRER LA LISTA DE SCORE E IR TOMANDO DE A 2 Y RESTARLOS. SI LA DIFERENCIA
	 * ES MAYOR A CUTPOINT ENTONCES LA DIFERENCIA ENTRE EL SCORE DE ESAS 2 ARISTAS
	 * ES MUY GRANDE POR LO QUE LAS ARISTAS QUE SIGUEN YA NO IMPORTAN
	 */
	@Override
	public List<Dependency> getListFromThreshold(List<Dependency> list) {
		List<Dependency> filteredDependencies = new ArrayList<>();
		if (!list.isEmpty()) {
			// SI LLEGARA A PASAR QUE EXISTA UN NODO QUE ES VECINO DE TODOS NO HABRIA CON
			// QUIEN PREDECIR SUS FUTUROS VECINOS
			double score = list.get(0).getScore();// GUARDO EL PRIMER SCORE PARA PODER CALCULAR LA DIFERENCIA
			double cutpoint = score * percent;
            filteredDependencies.add(list.get(0));
			for (int i = 1; i <= list.size() - 1; i++) {
				if ((score - list.get(i).getScore()) > cutpoint) {
					break;
				}
				score = list.get(i).getScore();
                filteredDependencies.add(list.get(i));
			}
		}
		return filteredDependencies;
	}
}