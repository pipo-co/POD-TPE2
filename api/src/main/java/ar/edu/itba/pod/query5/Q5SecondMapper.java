package ar.edu.itba.pod.query5;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class Q5SecondMapper implements Mapper<String, Q5TransitionalAnswer, Integer, String> {

    private static int truncateLastDigit(final int number) {
        return (number / 10) * 10;
    }

    @Override
    public void map(final String collectionName, final Q5TransitionalAnswer ans, final Context<Integer, String> context) {
        final int group = truncateLastDigit(ans.getTreeCount());
        if(group > 0) {
            context.emit(group, ans.getStreet());
        }
    }
}
