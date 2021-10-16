package ar.edu.itba.pod.query1;

import ar.edu.itba.pod.models.Tree;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;


public class Q1Mapper implements Mapper<String, Tree, String, Integer> {

    @Override
    public void map(final String neighbourhood, final Tree tree, final Context<String, Integer> context) {
        context.emit(neighbourhood, 1);
    }
}
