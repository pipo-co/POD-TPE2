package ar.edu.itba.pod.models;

import java.io.IOException;

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
        this.name       = name;
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
    public String toString() {
        return "Neighbourhood{" +
            "name='" + name + '\'' +
            ", population=" + population +
            '}';
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Neighbourhood other = (Neighbourhood) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public String getName() {
        return name;
    }
    public long getPopulation() {
        return population;
    }
}
