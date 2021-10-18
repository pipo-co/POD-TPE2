package ar.edu.itba.pod;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import ar.edu.itba.pod.models.Tree;

public class HoodTreesMapper implements Mapper<String, Tree, String, String> {

    @Override
    public void map(String hood, Tree tree, Context<String, String> context) {
        context.emit(hood, tree.getName());
    }
}
