package ar.edu.itba.pod.utils.mappers;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class NopMapper<Key, Value> implements Mapper<Key, Value, Key, Value> {

    @Override
    public void map(final Key key, final Value value, final Context<Key, Value> context) {
        context.emit(key, value);
    }
}
