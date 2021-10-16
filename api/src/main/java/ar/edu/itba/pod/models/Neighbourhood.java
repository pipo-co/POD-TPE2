package ar.edu.itba.pod.models;

public class Neighbourhood {
    
    final private String    name;
    final private long      population;
    
    public Neighbourhood(final String name, final long population) {
        this.name       = name;
        this.population = population;
    }

    public String getName() {
        return name;
    }
    public long getPopulation() {
        return population;
    }
}
