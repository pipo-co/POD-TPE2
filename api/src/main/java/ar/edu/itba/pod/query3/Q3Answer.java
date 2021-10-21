package ar.edu.itba.pod.query3;

import java.io.IOException;
import java.util.Map;

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
        this.hoodName           = hoodName;
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
