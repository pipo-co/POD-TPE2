package ar.edu.itba.pod;

import static java.util.Objects.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.hazelcast.mapreduce.Collator;

public class SortCollator<Key, Value, Answer extends Comparable<? super Answer>> implements Collator<Map.Entry<Key, Value>, List<Answer>> {

    private final Function<Entry<Key, Value>, Answer>   entryToAnswer;
    private final Comparator<? super Answer>            comparator;

    public SortCollator(final Function<Entry<Key, Value>, Answer> toAnswer, final Comparator<? super Answer> comparator) {
        this.entryToAnswer  = requireNonNull(toAnswer);
        this.comparator     = requireNonNull(comparator);
    }

    public SortCollator(final Function<Entry<Key, Value>, Answer> toAnswer) {
        this(toAnswer, Comparator.naturalOrder());
    }

    @Override
    public List<Answer> collate(final Iterable<Map.Entry<Key, Value>> entryIterator) {
        final List<Answer> response = new ArrayList<>();

        for(final Map.Entry<Key, Value> entry : entryIterator) {
            response.add(entryToAnswer.apply(entry));
        }

        response.sort(comparator);

        return response;
    }
}
