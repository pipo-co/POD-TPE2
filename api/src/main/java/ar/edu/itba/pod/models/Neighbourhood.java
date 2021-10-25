package ar.edu.itba.pod.models;

import static java.util.Objects.*;

import java.io.IOException;
import java.util.Objects;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Neighbourhood implements DataSerializable {
    
    private String    name;
    private long      population;

    private Neighbourhood() {
        // Serialization
    }

    public Neighbourhood(final String name, final long population) {
        this.name       = requireNonNull(name);
        this.population = population;
    }

    @Override
    public void writeData(final ObjectDataOutput out) throws IOException {
        out.writeUTF(name);
        out.writeLong(population);
    }

    @Override
    public void readData(final ObjectDataInput in) throws IOException {
        name        = in.readUTF();
        population  = in.readLong();
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(!(o instanceof Neighbourhood)) return false;
        final Neighbourhood that = (Neighbourhood) o;
        return population == that.population
            && Objects.equals(name, that.name)
            ;
    }

    @Override
    public int hashCode() {
        return hash(name, population);
    }

    @Override
    public String toString() {
        return "Neighbourhood{" +
            "name='" + name + '\'' +
            ", population=" + population +
            '}';
    }

    public String getName() {
        return name;
    }
    public long getPopulation() {
        return population;
    }
}
