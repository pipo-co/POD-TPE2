package ar.edu.itba.pod.client;

import java.util.function.Function;

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

    enum DataSources {
        BUE(csv -> new Tree(csv[2], csv[4], csv[7])),
        VCU(csv -> new Tree(csv[2], csv[6], csv[12])),
        ;

        final Function<String[], Tree> function;

        private DataSources(final Function<String[], Tree> fromCsv) {
            this.function = fromCsv;
        }

        public Tree fromCSV(final String[] csvLine) {
            return function.apply(csvLine);
        }
    }
}
