package edu.linkprediction.threshold;

import java.util.ArrayList;
import java.util.List;

import edu.linkprediction.parser.Dependency;

public class FirstElements extends Threshold {

	private final int k;

    public FirstElements(int k) {
        this.k = k;
        super.name = "First " + k + " Elements";
    }

	@Override
	public List<Dependency> getListFromThreshold(List<Dependency> list) {
		List<Dependency> finalList = new ArrayList<>();
		if (!list.isEmpty()) {
			int maxSize = list.size() < k ? (list.size() - 1) : k;
			return list.subList(0, maxSize);
		}
		return finalList;
	}

}
