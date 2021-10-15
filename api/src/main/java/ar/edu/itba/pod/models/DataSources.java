package ar.edu.itba.pod.models;

import java.util.function.Function;

public enum DataSources {
    BUE(
        csv -> new Tree(csv[2], csv[4], csv[7]),
        csv -> new Neighbourhood(csv[0], Long.valueOf(csv[1]))
    ),
    VCU(
        csv -> new Tree(csv[2], csv[6], csv[12]),
        csv -> new Neighbourhood(csv[0], Long.valueOf(csv[1]))
    ),
    ;

    final Function<String[], Tree>          treeBuilder;
    final Function<String[], Neighbourhood> neighbourhoodBuilder;

    private DataSources(final Function<String[], Tree> treeBuilder, final Function<String[], Neighbourhood> neighbourhoodBuilder) {
        this.treeBuilder            = treeBuilder;
        this.neighbourhoodBuilder   = neighbourhoodBuilder;
    }

    public Tree treeFromCSV(final String[] csvLine) {
        return treeBuilder.apply(csvLine);
    }

    public Neighbourhood neighbourhoodFromCSV(final String[] csvLine) {
        return neighbourhoodBuilder.apply(csvLine);
    }
}