package ar.edu.itba.pod.query5;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import ar.edu.itba.pod.models.Tree;

public class Q5FirstMapper implements Mapper<Tree, Integer, String, Integer> {

    @Override
    public void map(final Tree tree, final Integer count, final Context<String, Integer> context) {
        context.emit(tree.getHoodStreet(), count);
    }
}
