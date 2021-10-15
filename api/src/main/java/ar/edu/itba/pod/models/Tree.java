package ar.edu.itba.pod.models;

public class Tree {

    private final String neighbourhoodName;

    private final String neighbourhoodStreet;
    
    private final String commonName;

    public Tree(String neighbourhoodName, String neighbourhoodStreet, String commonName) {
        this.neighbourhoodName = neighbourhoodName;
        this.neighbourhoodStreet = neighbourhoodStreet;
        this.commonName = commonName;
    }

    public String getNeighbourhoodName() {
        return neighbourhoodName;
    }



    public String getNeighbourhoodStreet() {
        return neighbourhoodStreet;
    }



    public String getCommonName() {
        return commonName;
    }
}
