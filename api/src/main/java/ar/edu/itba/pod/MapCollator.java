package ar.edu.itba.pod;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.hazelcast.mapreduce.Collator;

public class MapCollator<Key, Value, Answer> implements Collator<Map.Entry<Key, Value>, Void> {

    private final Function<Map.Entry<Key, Value>, Answer>   entryToAnswer;
    private final Consumer<Answer>                          callback;

    public MapCollator(final Function<Map.Entry<Key, Value>, Answer> toAnswer, final Consumer<Answer> callback) {
        this.entryToAnswer  = requireNonNull(toAnswer);
        this.callback       = requireNonNull(callback);
    }

    @Override
    public Void collate(final Iterable<Map.Entry<Key, Value>> entries) {
        for(final Map.Entry<Key, Value> entry : entries) {
            callback.accept(entryToAnswer.apply(entry));
        }

        return null; // void
    }
}
