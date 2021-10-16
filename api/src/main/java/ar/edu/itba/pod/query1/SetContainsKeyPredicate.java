package ar.edu.itba.pod.query1;

import static java.util.Objects.*;

import java.util.Set;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.KeyPredicate;

/*
 * Se podria generalizar para cualquier coleccion, pero requeriria recibir un function.
 * Solo vale la pena si surge la necesidad.
 * - Tobi
 */
public class SetContainsKeyPredicate<Key> implements KeyPredicate<Key>, HazelcastInstanceAware {

    // Configuration
    private final String   keySetName;

    private       Set<Key> keySet;

    public SetContainsKeyPredicate(final String keySetName) {
        this.keySetName = requireNonNull(keySetName);
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcast) {
        this.keySet = hazelcast.getSet(keySetName);
    }

    @Override
    public boolean evaluate(final Key key) {
        return keySet.contains(key);
    }
}
