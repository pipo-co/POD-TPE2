package ar.edu.itba.pod.query3;

import static java.util.Objects.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Q3Answer implements DataSerializable {
    
    private String  hoodName;
    private int     distinctSpecies;

    private Q3Answer() {
        // Serialization
    }

    public Q3Answer(final String hoodName, final int differentSpecies) {
        this.hoodName           = requireNonNull(hoodName);
        this.distinctSpecies    = differentSpecies;
    }

    public static Q3Answer fromEntry(final Map.Entry<String, Integer> entry) {
        return new Q3Answer(entry.getKey(), entry.getValue());
    }

    @Override
    public void readData(final ObjectDataInput input) throws IOException {
        hoodName            = input.readUTF();
        distinctSpecies     = input.readInt();
    }

    @Override
    public void writeData(final ObjectDataOutput output) throws IOException {
        output.writeUTF(hoodName);
        output.writeInt(distinctSpecies);
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(!(o instanceof Q3Answer)) return false;
        final Q3Answer q3Answer = (Q3Answer) o;
        return distinctSpecies == q3Answer.distinctSpecies
            && Objects.equals(hoodName, q3Answer.hoodName)
            ;
    }

    @Override
    public int hashCode() {
        return hash(hoodName, distinctSpecies);
    }

    @Override
    public String toString() {
        return "Q3Answer{" +
            "hood='" + hoodName + '\'' +
            ", differentTreeSpecies=" + distinctSpecies +
            '}';
    }

    public String getHoodName() {
        return hoodName;
    }
    public int getDistinctSpecies() {
        return distinctSpecies;
    }
}
