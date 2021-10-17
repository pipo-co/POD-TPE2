package ar.edu.itba.pod.query2;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import ar.edu.itba.pod.models.Neighbourhood;

public class Q2Answer implements Comparable<Q2Answer>, DataSerializable {

    private static final Comparator<Q2Answer> NATURAL_ORDER = Comparator
        .comparing   (Q2Answer::getHoodName)
        ;

    private String    hoodName;
    private long      hoodInhabitants;
    private String    treeName;
    private double    treesPerInhabitant;

    private Q2Answer() {
        // Serialization
    }

    public Q2Answer(final Neighbourhood hood) {
        hoodName = hood.getName();
        hoodInhabitants = hood.getPopulation();
    }
    

    @Override
    public int compareTo(Q2Answer o) {
        return NATURAL_ORDER.compare(this, o);
    }
    

    @Override
    public String toString() {
        return "Q2Answer{" +
            "hood='" + hoodName + '\'' +
            ", tree=" + treeName + '\'' +
            ", treesPerInhabitant=" + treesPerInhabitant +
            '}';
    }

    @Override
    public void readData(ObjectDataInput input) throws IOException {
        hoodName            = input.readUTF();
        treeName            = input.readUTF();
        treesPerInhabitant  = input.readDouble();
        
    }

    @Override
    public void writeData(ObjectDataOutput output) throws IOException {
        output.writeUTF(hoodName);
        output.writeUTF(treeName);
        output.writeDouble(treesPerInhabitant);
        
    }

    public String getHoodName() {
        return hoodName;
    }
    public String getTreeName() {
        return treeName;
    }
    public double getTreesPerInhabitant() {
        return treesPerInhabitant;
    }

    public void setTreeParams(final Map.Entry<String, Long> bestTree) {
        treeName = bestTree.getKey();
        //Piden que usemos solo dos decimales
        treesPerInhabitant = Double.parseDouble(new DecimalFormat("##.##").format(bestTree.getValue() / hoodInhabitants));
    }
}
