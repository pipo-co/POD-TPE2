package ar.edu.itba.pod;

import static java.util.Objects.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.hazelcast.mapreduce.Collator;

public class SortCollator<Key, Value, Answer extends Comparable<? super Answer>> implements Collator<Map.Entry<Key, Value>, List<Answer>> {

    private final EntryToSortableMapper<Key, Value, Answer> mapper;

    public SortCollator(final EntryToSortableMapper<Key, Value, Answer> mapper) {
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public List<Answer> collate(final Iterable<Map.Entry<Key, Value>> entryIterator) {
        final List<Answer> response = new ArrayList<>();

        for(final Map.Entry<Key, Value> entry : entryIterator) {
            response.add(mapper.toSortable(entry));
        }

        response.sort(Comparator.naturalOrder());

        return response;
    }

    @FunctionalInterface
    public interface EntryToSortableMapper<Key, Value, Sortable extends Comparable<? super Sortable>> {
        Sortable toSortable(final Map.Entry<Key, Value> entry);
    }
}
