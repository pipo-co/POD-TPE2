package ar.edu.itba.pod.query3;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Q3Answer implements DataSerializable {
    
    private String hoodName;
    private int differentSpecies;

    private Q3Answer() {
        // Serialization
    }

    public Q3Answer(final String hoodName, final int differentSpecies) {
        this.hoodName = hoodName;
        this.differentSpecies = differentSpecies;
    }

    @Override
    public String toString() {
        return "Q3Answer{" +
            "hood='" + hoodName + '\'' +
            ", differentTreeSpecies=" + differentSpecies +
            '}';
    }

    @Override
    public void readData(final ObjectDataInput input) throws IOException {
        hoodName            = input.readUTF();
        differentSpecies    = input.readInt();        
    }

    @Override
    public void writeData(final ObjectDataOutput output) throws IOException {
        output.writeUTF(hoodName);
        output.writeInt(differentSpecies);        
    }

    public String getHoodName() {
        return hoodName;
    }

    public int getDifferentSpecies() {
        return differentSpecies;
    }
}
