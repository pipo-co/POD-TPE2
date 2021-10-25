package ar.edu.itba.pod.query5;

import static java.util.Objects.*;

import com.hazelcast.mapreduce.KeyPredicate;

import ar.edu.itba.pod.models.Tree;

public class Q5KeyPredicate implements KeyPredicate<Tree> {

    private final String hood;
    private final String species;

    public Q5KeyPredicate(final String hood, final String species) {
        this.hood       = requireNonNull(hood);
        this.species    = requireNonNull(species);
    }

    @Override
    public boolean evaluate(final Tree tree) {
        return tree.getHoodName().equals(hood)
            && tree.getName().equals(species)
            ;
    }
}
