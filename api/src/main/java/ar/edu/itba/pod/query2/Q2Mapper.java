package ar.edu.itba.pod.query2;

import static java.util.Objects.*;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

public class Q2Mapper implements Mapper<String, Tree, Neighbourhood, String>, HazelcastInstanceAware {

    // Configuration
    private final String                        mapName;

    private       IMap<String, Neighbourhood>   hoodMap;

    public Q2Mapper(final String mapName) {
        this.mapName = requireNonNull(mapName);
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcast) {
        this.hoodMap = hazelcast.getMap(mapName);
        
    }


    @Override
    public void map(String hoodName, Tree tree, Context<Neighbourhood, String> context) {
        context.emit(hoodMap.get(hoodName), tree.getName());
    }
    
}
