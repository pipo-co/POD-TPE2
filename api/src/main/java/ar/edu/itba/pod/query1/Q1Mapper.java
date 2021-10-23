package ar.edu.itba.pod.query1;

import static java.util.Objects.*;

import java.util.Set;

import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.utils.mappers.NopMapper;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.LifecycleMapper;

public class Q1Mapper extends NopMapper<String, Integer> implements LifecycleMapper<String, Integer, String, Integer>, HazelcastInstanceAware {

    // Configuration
    private final String hoodsSetName;

    // Transient State
    private transient Set<String> hoods;

    public Q1Mapper(final String hoodsSetName) {
        this.hoodsSetName = requireNonNull(hoodsSetName);
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcast) {
        hoods = hazelcast.getSet(hoodsSetName);
    }

    @Override
    public void initialize(final Context<String, Integer> context) {
        // Nos garantizamos que todos los barrios esten en la respuesta, al menos con el valor 0
        hoods.forEach(hood -> context.emit(hood, 0));
    }

    @Override
    public void finalized(final Context<String, Integer> context) {
        // void
    }
}
