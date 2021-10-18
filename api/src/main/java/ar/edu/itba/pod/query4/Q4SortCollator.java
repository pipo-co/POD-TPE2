package ar.edu.itba.pod.query4;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import java.util.stream.Collectors;

import com.hazelcast.mapreduce.Collator;

public class Q4SortCollator<Key, Value, Answer> implements Collator<Map.Entry<Key, Value>, List<Answer>> {

    private final Function<Map.Entry<Key, Value>, List<Answer>>                   entryToAnswer;
    private final Comparator<? super Map.Entry<Key, List<Answer>>>                comparator;

    public Q4SortCollator(final Function<Map.Entry<Key, Value>, List<Answer>> toAnswer, final Comparator<? super Map.Entry<Key, List<Answer>>> comparator) {
        this.entryToAnswer  = requireNonNull(toAnswer);
        this.comparator     = requireNonNull(comparator);
    }

    // /** @throws ClassCastException si {@link Answer} no es Comparable */
    // @SuppressWarnings("unchecked")
    // public Q4SortCollator(final Function<Map.Entry<Key, Value>, List<Answer>> toAnswer) throws ClassCastException {
    //     this(toAnswer, (Comparator<? super Answer>) Comparator.naturalOrder());
    // }

    @Override
    public List<Answer> collate(final Iterable<Map.Entry<Key, Value>> entryIterator) {
        final Map<Key, List<Answer>> aux = new HashMap<>();

        for(final Map.Entry<Key, Value> entry : entryIterator) {
            aux.put(entry.getKey(), entryToAnswer.apply(entry));
        }

        return aux.entrySet().stream().sorted(comparator).flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
    }
}
