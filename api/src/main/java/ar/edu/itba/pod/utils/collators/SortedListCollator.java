package ar.edu.itba.pod.utils.collators;

import static java.util.Objects.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.hazelcast.mapreduce.Collator;

public class SortedListCollator<Key, Value, Answer> implements Collator<Map.Entry<Key, Value>, List<Answer>> {

    private final Function<Map.Entry<Key, Value>, Answer>   entryToAnswer;
    private final Comparator<? super Answer>                comparator;

    public SortedListCollator(final Function<Map.Entry<Key, Value>, Answer> toAnswer, final Comparator<? super Answer> comparator) {
        this.entryToAnswer  = requireNonNull(toAnswer);
        this.comparator     = requireNonNull(comparator);
    }

    /** @throws ClassCastException si {@link Answer} no es Comparable */
    @SuppressWarnings("unchecked")
    public SortedListCollator(final Function<Map.Entry<Key, Value>, Answer> toAnswer) throws ClassCastException {
        this(toAnswer, (Comparator<? super Answer>) Comparator.naturalOrder());
    }

    @Override
    public List<Answer> collate(final Iterable<Map.Entry<Key, Value>> entries) {
        final List<Answer> response = new ArrayList<>();

        for(final Map.Entry<Key, Value> entry : entries) {
            response.add(entryToAnswer.apply(entry));
        }

        response.sort(comparator);

        return response;
    }
}
