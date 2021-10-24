package ar.edu.itba.pod.query5;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;

public class Q5Answer implements DataSerializable, Comparable<Q5Answer> {

    private static final Comparator<Q5Answer> ORDER = Comparator
            .comparing      (Q5Answer::getGroup)
            .thenComparing  (Q5Answer::getStreetA)
            .thenComparing  (Q5Answer::getStreetB)
            ;

    private int     group;
    private String  streetA;
    private String  streetB;

    private Q5Answer() {
        // Serialization
    }

    public Q5Answer(final int group, final String firstStreet, final String secondStreet) {
        this.group = group;

        final boolean ordered = firstStreet.compareTo(secondStreet) < 0;
        this.streetA = ordered ? firstStreet  : secondStreet;
        this.streetB = ordered ? secondStreet : firstStreet;
    }

    @Override
    public void writeData(ObjectDataOutput output) throws IOException {
        output.writeInt(group);
        output.writeUTF(streetA);
        output.writeUTF(streetB);
    }

    @Override
    public void readData(ObjectDataInput input) throws IOException {
        group = input.readInt();
        streetA = input.readUTF();
        streetB = input.readUTF();
    }

    @Override
    public int compareTo(Q5Answer o) {
        return ORDER.compare(this, o);
    }


    public int getGroup() {
        return group;
    }

    public String getStreetA() {
        return streetA;
    }

    public String getStreetB() {
        return streetB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Q5Answer)) return false;
        Q5Answer q5Answer = (Q5Answer) o;
        return group == q5Answer.group
                && Objects.equals(streetA, q5Answer.streetA)
                && Objects.equals(streetB, q5Answer.streetB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, streetA, streetB);
    }

    @Override
    public String toString() {
        return "Q5Answer{" +
                "group=" + group +
                ", streetA='" + streetA + '\'' +
                ", streetB='" + streetB + '\'' +
                '}';
    }
}
