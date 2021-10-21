package ar.edu.itba.pod.utils.keyPredicates;

import static java.util.Objects.*;

import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.KeyPredicate;

public class MapContainsKeyPredicate<Key> implements KeyPredicate<Key>, HazelcastInstanceAware {

    public enum MapCollection {
        KEYS {
            @Override
            protected boolean contains(final Map<Object, Object> map, final Object key) {
                return map.containsKey(key);
            }
        },
        VALUES {
            @Override
            protected boolean contains(final Map<Object, Object> map, final Object key) {
                return map.containsValue(key);
            }
        },
        ;

        protected abstract boolean contains(final Map<Object, Object> map, final Object key);
    }

    // Configuration
    private final String            collectionName;
    private final MapCollection     mapCollection;

    // Transient State
    private transient Map<Object, Object> map;

    public MapContainsKeyPredicate(final String collectionName, final MapCollection mapCollection) {
        this.collectionName = requireNonNull(collectionName);
        this.mapCollection  = requireNonNull(mapCollection);
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcast) {
        this.map = hazelcast.getMap(collectionName);
    }

    @Override
    public boolean evaluate(final Key key) {
        return mapCollection.contains(map, key);
    }
}
