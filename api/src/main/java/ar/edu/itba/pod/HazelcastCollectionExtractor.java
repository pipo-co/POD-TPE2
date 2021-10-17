package ar.edu.itba.pod;

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
    MAP_KEYS {
        @Override
        public <T> Collection<T> extract(final HazelcastInstance hazelcast, final String collectionName) {
            return hazelcast.<T, Object>getMap(collectionName).keySet();
        }
    },
    MAP_VALUES {
        @Override
        public <T> Collection<T> extract(final HazelcastInstance hazelcast, final String collectionName) {
            return hazelcast.<Object, T>getMap(collectionName).values();
        }
    },
    MULTI_MAP_KEYS {
        @Override
        public <T> Collection<T> extract(final HazelcastInstance hazelcast, final String collectionName) {
            return hazelcast.<T, Object>getMultiMap(collectionName).keySet();
        }
    },
    MULTI_MAP_VALUES {
        @Override
        public <T> Collection<T> extract(final HazelcastInstance hazelcast, final String collectionName) {
            return hazelcast.<Object, T>getMultiMap(collectionName).values();
        }
    },
    ;

    public abstract <Key> Collection<Key> extract(final HazelcastInstance hazelcast, final String collectionName);
}
