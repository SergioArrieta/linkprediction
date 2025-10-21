package edu.linkprediction.parser;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Dependency {

    private String nodoA;
    private String nodoB;
    private double score = 0;

    public Dependency(String nodoA, String nodoB, double score) {
        super();
        this.nodoA = nodoA;
        this.nodoB = nodoB;
        this.score = score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodoA == null) ? 0 : nodoA.hashCode());
        result = prime * result + ((nodoB == null) ? 0 : nodoB.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Dependency other = (Dependency) obj;
        return nodoA.equals(other.nodoA) && nodoB.equals(other.nodoB);
    }

    @Override
    public String toString() {
        return "Dependency [nodoA=" + nodoA + ", nodoB=" + nodoB + "]";
    }

}
