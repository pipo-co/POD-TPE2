package ar.edu.itba.pod.utils.collators;

import static java.util.Objects.*;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import com.hazelcast.mapreduce.Collator;

public class SortCollator<Key, Value, Answer> implements Collator<Map.Entry<Key, Value>, Void> {

    private final Function<Map.Entry<Key, Value>, Answer>   entryToAnswer;
    private final Comparator<? super Answer>                comparator;
    private final Consumer<Answer>                          callback;
    private final long                                      skip;
    private final long                                      limit;

    public SortCollator(
        final Function<Map.Entry<Key, Value>, Answer>   toAnswer,
        final Comparator<? super Answer>                comparator,
        final long                                      skip,
        final long                                      limit,
        final Consumer<Answer>                          callback) {

        this.entryToAnswer  = requireNonNull(toAnswer);
        this.comparator     = requireNonNull(comparator);
        this.callback       = requireNonNull(callback);
        this.skip           = 0;
        this.limit          = Long.MAX_VALUE;
    }

    public SortCollator(
        final Function<Map.Entry<Key, Value>, Answer>   toAnswer,
        final Comparator<? super Answer>                comparator,
        final Consumer<Answer>                          callback) {

        this(toAnswer, comparator, 0, Long.MAX_VALUE, callback);
    }

    @Override
    public Void collate(final Iterable<Map.Entry<Key, Value>> entries) {
        StreamSupport.stream(entries.spliterator(), false)
            .map    (entryToAnswer)
            .sorted (comparator)
            .skip   (skip)
            .limit  (limit)
            .forEach(callback)
            ;
        return null;
    }
}
