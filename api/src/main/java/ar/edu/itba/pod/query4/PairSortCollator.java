package ar.edu.itba.pod.query4;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.hazelcast.mapreduce.Collator;

public class PairSortCollator<Key, Value, Answer> implements Collator<Map.Entry<Key, Value>, List<Answer>> {

    private final Function<Map.Entry<Key, Value>, Answer>       entryToAnswer;
    private final Comparator<? super Answer>                    comparator;

    public PairSortCollator(final Function<Map.Entry<Key, Value>, Answer> toAnswer, final Comparator<? super Answer> comparator) {
        this.entryToAnswer  = requireNonNull(toAnswer);
        this.comparator     = requireNonNull(comparator);
    }


    /** @throws ClassCastException si {@link Answer} no es Comparable */
    @SuppressWarnings("unchecked")
    public PairSortCollator(final Function<Map.Entry<Key, Value>, Answer> toAnswer) throws ClassCastException {
        this(toAnswer, (Comparator<? super Answer>) Comparator.naturalOrder());
    }

    @Override
    public List<Answer> collate(final Iterable<Entry<Key, Value>> entryIterator) {
        final List<Answer> aux = new ArrayList<>();
    
        for(final Map.Entry<Key, Value> entry : entryIterator) {
            aux.add(entryToAnswer.apply(entry));
        }
        
        final List<Answer> response = new ArrayList<>();

        for (final Answer answer : response) {
            
        }
        
        response.sort(comparator);

        return response;
    }
    
}
