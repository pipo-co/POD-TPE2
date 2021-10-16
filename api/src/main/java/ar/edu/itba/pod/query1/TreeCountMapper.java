package ar.edu.itba.pod.query1;

import java.util.Collection;

import ar.edu.itba.pod.models.Tree;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;


public class TreeCountMapper implements Mapper<String, Collection<Tree>, String, Integer> {

    @Override
    public void map(String neighbourhood, Collection<Tree> trees, Context<String, Integer> context) {
        context.emit(neighbourhood, trees.size());
    }
    
}
