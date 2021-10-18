package ar.edu.itba.pod.query2;

import static java.util.Objects.*;

import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

public class Q2Mapper implements Mapper<String, Tree, Neighbourhood, String>, HazelcastInstanceAware {

    // Configuration
    private final       String                        mapName;

    // Transient State
    private transient   Map<String, Neighbourhood>    hoodMap;

    public Q2Mapper(final String mapName) {
        this.mapName = requireNonNull(mapName);
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcast) {
        this.hoodMap = hazelcast.getMap(mapName);
    }

    @Override
    public void map(final String hoodName, final Tree tree, final Context<Neighbourhood, String> context) {
        context.emit(hoodMap.get(hoodName), tree.getName());
    }
}
