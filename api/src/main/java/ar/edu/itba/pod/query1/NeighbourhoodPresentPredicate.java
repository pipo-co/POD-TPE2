package ar.edu.itba.pod.query1;

import java.util.Set;
import java.util.stream.Collectors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.KeyPredicate;

import ar.edu.itba.pod.models.Neighbourhood;

public class NeighbourhoodPresentPredicate implements KeyPredicate<String>, HazelcastInstanceAware{

    private Set<String> neighbourhoodNames;
    
    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        neighbourhoodNames = hazelcastInstance.<Neighbourhood, Long>getMap("g16-neighbourhood")
        .keySet()
        .stream()
        .map(Neighbourhood::getName)
        .collect(Collectors.toSet());
        
    }

    @Override
    public boolean evaluate(String neighbourhoodName) {
        
        return neighbourhoodNames.contains(neighbourhoodName);
    }
    
}
