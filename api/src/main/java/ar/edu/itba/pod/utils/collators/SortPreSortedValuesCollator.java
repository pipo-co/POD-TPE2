package ar.edu.itba.pod.utils.collators;

import static java.util.Objects.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import com.hazelcast.mapreduce.Collator;

public class SortPreSortedValuesCollator<Key, Answer> implements Collator<Map.Entry<Key, Collection<Answer>>, Void> {

    private final Comparator<? super Map.Entry<Key, Collection<Answer>>> comparator;
    private final Consumer<Answer> callback;

    public SortPreSortedValuesCollator(final Comparator<? super Key> comparator, final Consumer<Answer> callback) {
        this.comparator = Map.Entry.comparingByKey(comparator);
        this.callback   = requireNonNull(callback);
    }

    @Override
    public Void collate(final Iterable<Map.Entry<Key, Collection<Answer>>> entries) {
        StreamSupport.stream(entries.spliterator(), false)
            .sorted(comparator)
            .flatMap(e -> e.getValue().stream())
            .forEach(callback)
            ;
        return null;
    }
}
