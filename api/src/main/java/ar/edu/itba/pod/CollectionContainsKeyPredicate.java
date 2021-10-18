package ar.edu.itba.pod;

import static java.util.Objects.*;

import java.util.Collection;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.KeyPredicate;

public class CollectionContainsKeyPredicate<Key> implements KeyPredicate<Key>, HazelcastInstanceAware {

    // Configuration
    private final       String                        collectionName;
    private final       HazelcastCollectionExtractor  collectionExtractor;

    // Transient State
    private transient   Collection<Key>               keyCollection;

    public CollectionContainsKeyPredicate(final String collectionName, final HazelcastCollectionExtractor collectionExtractor) {
        this.collectionName         = requireNonNull(collectionName);
        this.collectionExtractor    = requireNonNull(collectionExtractor);
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcast) {
        this.keyCollection = collectionExtractor.extract(hazelcast, collectionName);
    }

    @Override
    public boolean evaluate(final Key key) {
        return keyCollection.contains(key);
    }
}
