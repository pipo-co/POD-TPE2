package ar.edu.itba.pod.query1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hazelcast.mapreduce.Collator;

public class SortCollator implements Collator<Map.Entry<String,Integer>, List<String>> {

    private static final String SEPARATOR = ";";

    @Override
    public List<String> collate(Iterable<Entry<String, Integer>> iterable) {
        final List<String> response = new ArrayList<>();
        for (Entry<String,Integer> entry : iterable) {
            response.add(entry.getKey() + SEPARATOR + entry.getKey());
        }
        return response;
    }


    

    
}
