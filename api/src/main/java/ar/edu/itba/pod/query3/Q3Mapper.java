package ar.edu.itba.pod.query3;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import ar.edu.itba.pod.models.Tree;

public class Q3Mapper implements Mapper<String, Tree, String, String> {

    @Override
    public void map(final String hood, final Tree tree, final Context<String, String> context) {
        context.emit(hood, tree.getName());
    }
}
