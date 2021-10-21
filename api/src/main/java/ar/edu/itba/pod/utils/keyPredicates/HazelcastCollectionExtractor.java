package ar.edu.itba.pod.utils.keyPredicates;

import java.util.Collection;

import com.hazelcast.core.HazelcastInstance;

public enum HazelcastCollectionExtractor {
    SET {
        @Override
        public <T> Collection<T> extract(final HazelcastInstance hazelcast, final String collectionName) {
            return hazelcast.getSet(collectionName);
        }
    },
    LIST {
        @Override
        public <T> Collection<T> extract(final HazelcastInstance hazelcast, final String collectionName) {
            return hazelcast.getList(collectionName);
        }
    },
    QUEUE {
        @Override
        public <T> Collection<T> extract(final HazelcastInstance hazelcast, final String collectionName) {
            return hazelcast.getQueue(collectionName);
        }
    },
    ;

    public abstract <Key> Collection<Key> extract(final HazelcastInstance hazelcast, final String collectionName);
}
